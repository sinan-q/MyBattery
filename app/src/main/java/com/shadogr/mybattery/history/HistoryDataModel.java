package com.shadogr.mybattery.history;

import android.text.format.DateFormat;

import com.shadogr.mybattery.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HistoryDataModel {

    int SOnT;
    int rMColor;
    String lB;
    String rM;
    String lT;
    String lM;
    String rB;
    String rT;
    String time;
    String RomTest;
    int[] ids;
    float[] temp;
    private final String a = "";

    public HistoryDataModel(int batStart, int batEnd, long timeStart, long timeEnd, int SOT, float tempMax, float tempMin, int i, int j, int scrnOnPerc,boolean boo) {
        SOnT = SOT * 1000;
        temp = new float[]{tempMax, tempMin};
        ids = new int[]{i, j,batStart,batEnd,SOnT};
        lM = batStart + "% to " + batEnd + "%";
        int changeInBatLevel = batEnd - batStart;
        rT = getFormattedDate(timeStart);
        int durationInMillis = (int) (timeEnd - timeStart);
        String lt_text;
        String rm_text = "";
        if (boo) this.time = getFormattedDate(timeEnd);
        if (changeInBatLevel > 0) {
            lt_text = "Charged for ";
            lB = String.join(a, "Min: ", String.valueOf(tempMax), "°C & Max: ", String.valueOf(tempMin), "°C");
            rMColor = R.color.charging_green;
            rm_text = "+";
            int speed = durationInMillis / changeInBatLevel;
            rB = String.join(a, formatSOT(speed), "/%");
        } else {
            lt_text = "Used for ";
            lB = String.join(a, formatSOT(SOnT), " (", String.valueOf(SOnT * 100 / durationInMillis), "%)");
            rMColor = R.color.temp_red;
            String speed = new DecimalFormat("###.##").format((float) (scrnOnPerc) * 3600 / (SOT));
            rB = String.join(a, speed, "%/h");
        }
        lT = String.join(a, lt_text, formatSOT(durationInMillis));
        rM = String.join(a, rm_text, String.valueOf(changeInBatLevel), "%");

    }

    private String formatSOT(int inMillis) {
        String k = "", pattern = "HH'h' mm'm' ";
        long DAY = 1000 * 60 * 60 * 24;
        SimpleDateFormat df;
        if (inMillis < 1000 * 60) pattern = "ss's'";
        else if (inMillis < 1000 * 60 * 60) pattern = "mm'm' ss's'";
        else if (inMillis > DAY) {
            k = inMillis / DAY + " day ";
            inMillis %= DAY;
        }
        df = new SimpleDateFormat(pattern, Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return String.join(a,k , df.format(new Date(inMillis)));
    }
    private String getFormattedDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DATE) == calendar.get(Calendar.DATE) ) return "Today " + DateFormat.format("h:mm aa", calendar);
        else return DateFormat.format("MMM d, h:mm aa", calendar).toString();

    }
    public HistoryDataModel(int i,int from,int to) {
        RomTest = i + " "+ from + " " + to ;
    }

}
