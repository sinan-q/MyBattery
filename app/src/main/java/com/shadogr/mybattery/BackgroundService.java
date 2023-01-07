package com.shadogr.mybattery;

import static android.app.Notification.CATEGORY_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

public class BackgroundService extends Service {
    private int prevBatLevel = -1;
    private int batLevel= -1;
    private int id;
    private int temp;
    private final int RESET_NOTIFICATION_BATTERY_LEVEL = 90;
    private int mATotal, currentNow, sot_on_counter,sot_off_counter, sot_scrn_on = 0, sot_scrn_off=0;
    private int currentScrnOn, currentScrnOff,perc_scr_on = 0, perc_scr_off = 0,romCount;
    private long time = 0,startSleepTime,last_scrn_on_time = 0,last_scrn_off_time=0,startTime=0;
    private final long HOUR=1000*60*60;
    private boolean screenState,wasCharging,isCharging;
    private final MutableLiveData<String> batLevelLiveData = new MutableLiveData<>();
    private final IBinder binder = new LocalBinder();
    private Notification notification;
    private DatabaseHandler db;
    private AppUsageDBHandler dp;
    private NotificationManager manager;
    private ServiceHandler serviceHandler;
    private BatteryManager bm;
    private static final String TAG = "BgService";
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            time = System.currentTimeMillis();
            switch (intent.getAction()) {
                case Intent.ACTION_BATTERY_CHANGED:
                    batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                    temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                    currentNow = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000;
                    batLevelLiveData.postValue(batLevel+"%");
                    wasCharging = mATotal<0;
                    isCharging = currentNow < 0;
                    if (prevBatLevel == -1) prevBatLevel = batLevel;
                    if (wasCharging != isCharging && mATotal!=0) saveToXml();
                    else {
                        mATotal = isDivider0(mATotal);
                        if (screenState) {
                            currentScrnOn = isDivider0(currentScrnOn);
                            if (!isCharging) saveCurrentApp();
                        } else currentScrnOff = isDivider0(currentScrnOff);
                    }
                    if (prevBatLevel != batLevel) {
                        if (prevBatLevel > batLevel) {
                            if (screenState) perc_scr_on++;
                            else perc_scr_off++;
                        }
                        saveToXml();
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    screenState = false;
                    if (last_scrn_on_time != 0) sot_on_counter += time - last_scrn_on_time;
                    last_scrn_off_time = time;
                    break;
                case Intent.ACTION_SCREEN_ON:
                    screenState = true;
                    if (last_scrn_off_time != 0) sot_off_counter += time - last_scrn_off_time;
                    last_scrn_on_time = time;
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }
    private int isDivider0(int value){
        if (value==0) return currentNow;
        else return (value + currentNow)/2;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startTime==0) {
            initVars();
            startSleepTime = getSleepTime();
            NotificationChannel CHANNEL_ID = new NotificationChannel("CHANNEL_ID", "My Background Service", NotificationManager.IMPORTANCE_NONE);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(CHANNEL_ID);
            notification = createNotification("0", "0");
            startForeground(1, notification);
            Message msg = serviceHandler.obtainMessage();
            msg.arg1 = startId;
            serviceHandler.sendMessage(msg);
            startTime = time = System.currentTimeMillis();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (screenState = pm.isInteractive()) last_scrn_on_time = time;
            db = new DatabaseHandler(this);
            dp = new AppUsageDBHandler(this);

            id = db.getLogCount();
            new Thread(() -> new CheckRom().start(getBaseContext())).start();

            romCount = db.getRomCount();
            if (id != 0 && db.getLog(id - 1).getBatLvl() == -1) db.updateLog(new DbObject(id - 1, time/1000));
            else db.addLog(new DbObject(id++, time/1000));
            bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        }
        return START_STICKY;
    }

    private void saveToXml() {
        if (screenState && last_scrn_on_time != 0) {
            sot_on_counter += time - last_scrn_on_time;
            last_scrn_on_time = time;
        }else if (!screenState && last_scrn_off_time != 0) {
            sot_off_counter += time - last_scrn_off_time;
            last_scrn_off_time = time;
        }
        if (!wasCharging) notifyNotification(true);
        else if (!isCharging && batLevel>=RESET_NOTIFICATION_BATTERY_LEVEL) notifyNotification(false);
        db.addLog(new DbObject(id++, time/1000, prevBatLevel = batLevel, temp, mATotal, sot_on_counter / 1000, currentScrnOff, currentScrnOn, screenState));
        initVars();
    }

    private Notification createNotification(String on, String off) {
        return new Notification.Builder(this, "CHANNEL_ID")
                .setStyle(new Notification.BigTextStyle().bigText(
                        "Screen Off : " + off +"%/h \n" +
                        "Deep Sleep : "+ formatSOT(getSleepTime() - startSleepTime)))
                .setContentTitle("Screen On : " + on + "%/h" )
                .setContentText("Screen Off : " + off +"%/h" )
                .setCategory(CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_stat_name)
                .build();
    }

    private void notifyNotification(boolean mode) {
        DecimalFormat def = new DecimalFormat("###.##");
        String speed_on="0.0";
        String speed_off="0.0";
        if (mode){
        sot_scrn_on+=sot_on_counter;
        sot_scrn_off+=sot_off_counter;
        if (perc_scr_on  !=0) speed_on = def.format((float) (perc_scr_on) * HOUR / (sot_scrn_on));
        if (perc_scr_off !=0) speed_off = def.format((float) (perc_scr_off) * HOUR / (sot_scrn_off));
        }else {
            sot_scrn_on = 0;sot_scrn_off = 0; perc_scr_off = 0; perc_scr_on = 0 ;startSleepTime = getSleepTime();
        }
        notification = createNotification(formatSOT(sot_scrn_on)  + " • "+ perc_scr_on + "% • " + speed_on  ,
                 formatSOT(sot_scrn_off) + " • " + perc_scr_off + "% • " + speed_off );
        manager.notify(1, notification);
    }
    private String formatSOT(long inMillis){
        String k="",pattern = "HH'h' mm'm' ";
        SimpleDateFormat df;
        long DAY = 1000 * 60 * 60 * 24;
        if (inMillis<1000*60) pattern = "ss's'";
        else if (inMillis<HOUR) pattern = "mm'm' ss's'";
        else if (inMillis> DAY)
        {
            k = inMillis/ DAY + " day ";
            inMillis %= DAY; }
        df = new SimpleDateFormat(pattern, Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return k + df.format(new Date(inMillis));
    }
    private long getSleepTime(){
        return SystemClock.elapsedRealtime() - SystemClock.uptimeMillis();
    }

    private void saveCurrentApp() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time-1000*1000, time);
        if (appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
            for (UsageStats usageStats : appList) mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            String pkgName = Objects.requireNonNull(mySortedMap.get(mySortedMap.lastKey())).getPackageName();
            if (pkgName !=null) {
                DbObject dbUsage = dp.getUsage(pkgName, romCount);
                if (dbUsage == null) try {
                    ApplicationInfo info = getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
                    String appName = (String) getPackageManager().getApplicationLabel(info);
                    dp.addUsage(new DbObject(pkgName, appName, currentNow, 1), romCount);
                } catch (PackageManager.NameNotFoundException ignored) {}
                else dp.updateUsage(new DbObject(pkgName, dbUsage.getUsageName(), dbUsage.getUsageCurrent() + currentNow, dbUsage.getUsageCurrentCount() + 1), romCount);
            }
        }
    }

    private void initVars() {
        mATotal = 0;
        currentScrnOff = 0;
        currentScrnOn = 0;
        sot_on_counter = 0;
        sot_off_counter = 0;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.gc();
        return true;
    }

    LiveData<String> getBatLevel() {
        return batLevelLiveData;
    }

    public String getCurrentNow() {
        return currentNow+"mA";
    }

    public String getTemp() {
        return (float) (temp/ 10) + "°C";
    }

    public class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            BackgroundService.this.registerReceiver(broadcastReceiver, intentFilter);
        }
    }


}
