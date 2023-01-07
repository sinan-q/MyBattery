package com.shadogr.mybattery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LogDataModel {

    public int statusColor,tempColor;
    public String status,time,current,level,temp,duration;

    public LogDataModel(DbObject arr) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        int l = arr.getBatLvl();
        float te=(float) arr.getTemp()/10;
        int c = arr.getCurrent();
        int duration = arr.getSot();
        this.time = formatter.format(new Date(arr.getTime()*1000));
        String CONSTANT = "----";
        if (c==0) this.duration = CONSTANT;
        else this.duration = formatSOT(duration*1000);

        if (l!=-1){
        level = l +"%";
        temp = te +"°C";
        if (te>40) if (te>42) tempColor = R.color.temp_red; else tempColor = R.color.temp_yellow;
        else tempColor = R.color.text;
        current = c + "mA";
        if (arr.getScreenState()) status = "ON";
        else status = "OFF";
        if (c>0) statusColor = R.color.text;
        else statusColor = R.color.charging_green; }
        else{
            status = CONSTANT;
            current = CONSTANT;
            level = CONSTANT;
            temp = CONSTANT;
            statusColor = R.color.design_default_color_on_primary;
            tempColor = R.color.design_default_color_on_primary;
        }
    }
    private String formatSOT(int inMillis){
        String k="",pattern = "HH'h' mm'm' ";
        long DAY = 1000*60*60*24;
        SimpleDateFormat df;
        if (inMillis<1000*60) pattern = "ss's'";
        else if (inMillis<1000*60*60) pattern = "mm'm' ss's'";
        else if (inMillis>DAY)
        {
            k = inMillis/DAY + " day ";
            inMillis %= DAY; }
        df = new SimpleDateFormat(pattern, Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return k + df.format(new Date(inMillis));
    }

}
