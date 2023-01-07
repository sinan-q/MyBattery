package com.shadogr.mybattery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OnBootService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        Intent intent = new Intent(context, BackgroundService.class);
        context.startForegroundService(intent);
        Toast.makeText(context, "MyBattery Starting", Toast.LENGTH_SHORT).show();
    }


}
