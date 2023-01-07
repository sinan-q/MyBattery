package com.shadogr.mybattery;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CheckRom {
    public DatabaseHandler db;
    public AppUsageDBHandler dp;
    int db_id;
    int db_build_time;
    String db_kernel;
    String db_rom;
    String db_prop_rom;
    int db_count;
    DbObject arr;
    ArrayList<String> lines;

    public void newRom() {
        lines = getFromShell("getprop ");
        for (String line : lines) {
            String[] prop = line.split(":");
            if (prop[0].endsWith("display.version]")) {
                db_rom = prop[1].substring(2, prop[1].length() - 1);
                db_prop_rom = prop[0].split("\\.")[1];
                lines = getFromShell("getprop ro." + db_prop_rom + ".version");
                for (String s1 : lines.get(0).split("-"))
                    if (s1.startsWith("2") && s1.length() == 8) {
                        db_build_time = Integer.parseInt(s1);
                        addRom();
                    }
                break;
            }

        }
    }

    public void addRom() {
        db.addRom(new DbObject(db_id+1, db_build_time, db_kernel, db_rom, db_count, db_prop_rom));
        dp.addRom(db_id+1);
    }

    public void start(Context context) {
        db = new DatabaseHandler(context);
        dp = new AppUsageDBHandler(context);
        db_id = db.getRomCount();
        db_count = db.getLogCount();
        db_kernel = System.getProperty("os.version");
        if (db_id != 0) {
            arr = db.getRom(db_id);
            db_prop_rom = arr.getRomProp();

            lines = getFromShell("getprop ro." + db_prop_rom + ".version");

            if (lines.get(0).equals("")) newRom();
            else for (String s1 : lines.get(0).split("-"))
                if (s1.startsWith("2") && s1.length() == 8) {
                    if (Integer.parseInt(s1) != arr.getRomTime()) {
                        Log.d("TAG", "start: "+s1 + " " + arr.getRomTime());
                        db_build_time = Integer.parseInt(s1);
                        db.updateRom(db_count - 1);
                        db_rom = getFromShell("getprop ro." + db_prop_rom + ".display.version").get(0);
                        addRom();
                    } else if (!arr.getRomKernel().equals(db_kernel)) {
                        db_rom = arr.getRomName();
                        db.updateRom(db_count - 1);
                        addRom();
                    }
                    break;
                }

        } else {
            addRom();
        }
    }

    public ArrayList<String> getFromShell(String input) {
        String line;
        ArrayList<String> result = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec(input);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) result.add(line);
            return result;
        } catch (IOException ignored) {
        }
        return null;
    }
}
