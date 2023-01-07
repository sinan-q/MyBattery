package com.shadogr.mybattery.appusage;

import android.graphics.Bitmap;

import java.text.DecimalFormat;

public class AppUsageDataModel {
    String pkgName;
    StringBuilder currentText = new StringBuilder();
     String appName;
     Bitmap icon;
     int current;



    public AppUsageDataModel(Bitmap icon,String appName,int current,int[] currentInt ) {
       this.appName = appName;
       this.icon = icon;
       this.current = current;
       int length = currentInt.length;
       if (length>2) for (int j : currentInt) init(j);
       else init(current);

    }
    public AppUsageDataModel(Bitmap icon,String appName,int current,String pkgName) {
        this.appName = appName;
        this.icon = icon;
        this.current=current;
        this.pkgName = pkgName;
        init(current);
    }
    private void init(int value) {
        float curr = (float) value / 50;
        DecimalFormat def = new DecimalFormat("###.##");
        currentText.append(": ").append(def.format(curr)).append("%/h  (").append(value).append("mA) \n");
    }
}
