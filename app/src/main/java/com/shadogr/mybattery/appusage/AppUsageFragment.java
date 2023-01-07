package com.shadogr.mybattery.appusage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.shadogr.mybattery.AppUsageDBHandler;
import com.shadogr.mybattery.DatabaseHandler;
import com.shadogr.mybattery.DbObject;
import com.shadogr.mybattery.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class AppUsageFragment extends Fragment {
    private final ArrayList<AppUsageDataModel> selectedArray = new ArrayList<>();
    private final ArrayList<AppUsageDataModel> fullArray = new ArrayList<>();
    private final ArrayList<AppUsageDataModel> fullArrayWithOldData = new ArrayList<>();
    private final Comparator<AppUsageDataModel> UsageCurrent = (s1, s2) -> {
        int s1UsageCurrent = s1.current;
        int s2UsageCurrent = s2.current;
        return s2UsageCurrent - s1UsageCurrent;
    };
    RecyclerView mainRV;
    AppUsageDBHandler db;
    String tableName;
    Button openUsageSettings;
    SwitchCompat switchCompat;
    private Bitmap icon;
    private AppUsageAdapter adapter;
    AppUsageAdapter adapterFull;

    public AppUsageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.appusage_fragment, container, false);
        SwitchCompat switchCompat2 = view.findViewById(R.id.switch2);
        switchCompat = view.findViewById(R.id.switch1);
        openUsageSettings = view.findViewById(R.id.open_usage_settings_btn);
        db = new AppUsageDBHandler(requireContext());
        DatabaseHandler dp = new DatabaseHandler(requireContext());
        int tablesSize = dp.getRomCount();

        ArrayList<DbObject> dbArray = db.getNonNullUsage(tablesSize);
        Bitmap def_icon = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.def_app_icon);
        int[] current = new int[tablesSize];
        icon = def_icon;
        for (DbObject key : dbArray) {
            String pkgName = key.getUsageId();
            AppUsageDataModel dataModel = new AppUsageDataModel(icon, key.getUsageName(), key.getUsageCurrent() / key.getUsageCurrentCount(),pkgName);
            if (key.getUsageCurrentCount() > 100) selectedArray.add(dataModel);
            fullArray.add(dataModel);
        }
        new Thread(() -> {
            for (int i = 0; i < selectedArray.size();i++) {
                String pkgName = selectedArray.get(i).pkgName;
                File imgPath = new File(requireContext().getDataDir().getPath()+"/Files/AppIcons/", pkgName);
                if (imgPath.exists()) appIconChange(BitmapFactory.decodeFile(imgPath.getPath()),i);
                else try {
                    icon = getAppIcon(pkgName);
                    appIconChange(icon,i);
                } catch (PackageManager.NameNotFoundException ignored) { }
            }
            for (int i = 0; i < fullArray.size();i++) {
                String pkgName = fullArray.get(i).pkgName;
                File imgPath = new File(requireContext().getDataDir().getPath()+"/Files/AppIcons/", pkgName);
                if (imgPath.exists()) fullArray.get(i).icon=BitmapFactory.decodeFile(imgPath.getPath());
                else try { fullArray.get(i).icon = getAppIcon(pkgName); } catch (PackageManager.NameNotFoundException ignored) { }
            }
        }).start();
        selectedArray.sort(UsageCurrent);
        fullArray.sort(UsageCurrent);


        mainRV = view.findViewById(R.id.main_rv);
        adapter = new AppUsageAdapter(selectedArray);
        adapterFull = new AppUsageAdapter(fullArray);
        mainRV.setAdapter(adapter);
        mainRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


        if (db.isEmpty(tableName)) {
            switchCompat.setText(R.string.switch_no_data);
            switchCompat.setClickable(false);
            openUsageSettings.setVisibility(View.VISIBLE);
            openUsageSettings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));
        } else {
            openUsageSettings.setVisibility(View.GONE);
            switchCompat.setClickable(true);
        }

        switchCompat.setOnClickListener(v -> {
            if (switchCompat.isChecked()) mainRV.swapAdapter(adapterFull,false);
            else mainRV.swapAdapter(adapter,false);
        });

        switchCompat2.setOnClickListener(v -> {
            if (!switchCompat2.isChecked()) mainRV.swapAdapter(adapterFull,false);
            switchCompat.setChecked(true);
        });
        return view;
    }
    private void appIconChange(Bitmap icon,int i) {
    requireActivity().runOnUiThread(() -> {
        adapter.adapterArrayList.get(i).icon = icon;
        adapter.notifyItemChanged(i);
    });
    }


    @Override
    public void onDestroy() {
        icon = null;
        fullArray.clear();
        selectedArray.clear();
        fullArrayWithOldData.clear();
        mainRV.setAdapter(null);
        super.onDestroy();
    }

    private Bitmap getAppIcon(String pkgName) throws PackageManager.NameNotFoundException {
        Bitmap bitmap;
        Drawable icon = requireContext().getPackageManager().getApplicationIcon(pkgName);
        bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);
        FileOutputStream fos = null;
        try {
            File dir = new File(requireContext().getDataDir()+"/Files/AppIcons/");
            if (!dir.exists()) //noinspection ResultOfMethodCallIgnored
                dir.mkdir();
            fos = new FileOutputStream(new File(dir.getPath(), pkgName));
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 1, fos);
            fos.close();
        } catch (IOException e) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
        return bitmap;
    }

}