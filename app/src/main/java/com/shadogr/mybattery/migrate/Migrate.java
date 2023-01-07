package com.shadogr.mybattery.migrate;

import android.content.Context;

import com.shadogr.mybattery.AppUsageDBHandler;
import com.shadogr.mybattery.DatabaseHandler;

public class Migrate {
    private AppUsageDBHandler appUsageDBNew;
    private DatabaseHandler logDB;

    public void start(Context context){
        /*
        appUsageDBNew = new AppUsageDBHandler(context);
        logDB = new DatabaseHandler(context);
        int romCount = logDB.getRomCount();
        appUsageDBNew.createTable();

        int tablesSize = appUsageDB.getTablesNames().size();

        for (int i=0;i<romCount;i++){
            String tableName = "a" + i;
            ArrayList<DbObject> arr = appUsageDB.getAllUsage(tableName);
            if (i!=0) appUsageDBNew.addRom(tableName);
            for (DbObject DbO:arr) {
                if (!appUsageDBNew.getUsage(DbO.getUsageId()))
                {
                    appUsageDBNew.addUsage(new DbObject(DbO.getUsageId(), DbO.getUsageName(), DbO.getUsageCurrent(),  DbO.getUsageCurrentCount() ),tableName);
                }
                else appUsageDBNew.updateUsage(new DbObject(DbO.getUsageId(), DbO.getUsageName(), DbO.getUsageCurrent(),  DbO.getUsageCurrentCount() ),tableName);
            }
        }

*/

    }
}
