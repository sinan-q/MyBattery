package com.shadogr.mybattery;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class LogFragment extends Fragment{
    private BackgroundService mService;
    private boolean mBound = false;
    private TextView batTV;
    private TextView tempTV;
    private TextView currentTV;
    private static final String TAG = "log";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_fragment, container, false);
        Intent intent = new Intent(requireContext(),BackgroundService.class);
        requireContext().bindService(intent,connection, Context.BIND_AUTO_CREATE);
        batTV = view.findViewById(R.id.main_battery_tv);
        tempTV = view.findViewById(R.id.tv1);
        currentTV = view.findViewById(R.id.tv2);
        TextView currentUsageTV = view.findViewById(R.id.current_usage);
        TextView totalUsageTV = view.findViewById(R.id.total_usage);

        TextView kernelNameTV = view.findViewById(R.id.kernel_name);

        TextView romNameTV = view.findViewById(R.id.rom_name);

        DatabaseHandler db = new DatabaseHandler(requireContext());
        int count = db.getRomCount();
        DbObject dbObject = db.getRom(count);
        int lastId = dbObject.getRomCount();
        DbObject dbObject2 = db.getRom(0);
        int lastId2 = dbObject2.getRomCount();
        ArrayList<DbObject> arrayList = db.getLogFrom(lastId,db.getLogCount());
        ArrayList<DbObject> arrayList2 = db.getLogFrom(0,lastId2);
        String romName = dbObject.getRomName() + "\n" + dbObject.getRomTime();
        kernelNameTV.setText(dbObject.getRomKernel());
        romNameTV.setText(romName);
        int avgCharge = 0;
        int countd =0;
        for (DbObject object: arrayList) {
            if (object.getCurrent()>0) {
                avgCharge += object.getSot();
                countd++;
            }
        }
        if (countd!=0) currentUsageTV.setText(avgCharge/countd+"s");
        avgCharge = 0;
        countd =0;
        int avgNotCharge = 0;
        for (DbObject object: arrayList2) {
            if (object.getCurrent()>0) {
                avgCharge += object.getSot();
                countd++;
            }
            else if (object.getCurrent()<0) {
                avgNotCharge += object.getSot();
            }
        }
        totalUsageTV.setText(avgCharge/countd+"s");

        return view;
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        requireContext().unbindService(connection);
        mBound = false;
        super.onDestroy();
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.getBatLevel().observe(getViewLifecycleOwner(), string -> {
                batTV.setText(mService.getBatLevel().getValue());
                tempTV.setText(mService.getTemp() );
                currentTV.setText(mService.getCurrentNow());
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}