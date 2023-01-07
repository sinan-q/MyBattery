package com.shadogr.mybattery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String ROM_PROP = "prop";
    private final String TABLE_LOG = "logs";
    private final String LOG_ID = "id";
    private final String LOG_TIME = "time";
    private final String LOG_BAT_LVL = "battery_level";
    private final String LOG_TEMP = "temperature";
    private final String LOG_CURRENT = "current";
    private final String LOG_SOT = "sot";
    private final String LOG_SCREEN_OFF_CURRENT = "screen_off_current";
    private final String LOG_SCREEN_ON_CURRENT = "screen_on_current";
    private final String LOG_SCREEN_STATE = "screen_state";
    private final String TABLE_ROM = "rom";
    private final String ROM_ID = "id";
    private final String ROM_TIME ="build_time" ;
    private final String ROM_KERNEL = "kernel";
    private final String ROM_NAME = "rom_name";
    private final String ROM_COUNT = "count";

    public DatabaseHandler(Context context) {
        super(context, "BatteryLog.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOG_TABLE = "CREATE TABLE " + TABLE_LOG + "("
                + LOG_ID + " INTEGER PRIMARY KEY," + LOG_TIME + " INTEGER,"
                + LOG_BAT_LVL + " INTEGER,"
                + LOG_TEMP + " INTEGER,"
                + LOG_CURRENT + " INTEGER,"
                + LOG_SOT + " INTEGER,"
                + LOG_SCREEN_OFF_CURRENT + " INTEGER,"
                + LOG_SCREEN_ON_CURRENT + " INTEGER,"
                + LOG_SCREEN_STATE + " INTEGER" +")";
        String CREATE_ROM_TABLE = "CREATE TABLE " + TABLE_ROM + "("
                + ROM_ID + " INTEGER PRIMARY KEY,"
                + ROM_TIME + " INTEGER,"
                + ROM_KERNEL + " TEXT,"
                + ROM_NAME + " TEXT,"
                + ROM_COUNT + " INTEGER,"
                + ROM_PROP + " TEXT"+ ")";
        db.execSQL(CREATE_LOG_TABLE);
        db.execSQL(CREATE_ROM_TABLE);
    }
    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_LOG_TABLE = "CREATE TABLE " + TABLE_LOG + "("
                + LOG_ID + " INTEGER PRIMARY KEY," + LOG_TIME + " INTEGER,"
                + LOG_BAT_LVL + " INTEGER,"
                + LOG_TEMP + " INTEGER,"
                + LOG_CURRENT + " INTEGER,"
                + LOG_SOT + " INTEGER,"
                + LOG_SCREEN_OFF_CURRENT + " INTEGER,"
                + LOG_SCREEN_ON_CURRENT + " INTEGER,"
                + LOG_SCREEN_STATE + " INTEGER" +")";

        db.execSQL(CREATE_LOG_TABLE);
        db.close();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);

        // Create tables again
        onCreate(db);
    }

    public void addLog(DbObject dbObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LOG_ID,dbObject.getlogId());
        values.put(LOG_TIME, dbObject.getTime());
        values.put(LOG_BAT_LVL, dbObject.getBatLvl());
        values.put(LOG_TEMP,dbObject.getTemp());
        values.put(LOG_CURRENT,dbObject.getCurrent());
        values.put(LOG_SOT,dbObject.getSot());
        values.put(LOG_SCREEN_OFF_CURRENT,dbObject.getScrOffCurr());
        values.put(LOG_SCREEN_ON_CURRENT,dbObject.getScrOnCurr());
        values.put(LOG_SCREEN_STATE,dbObject.getScreenState());

        db.insert(TABLE_LOG, null, values);
        db.close();
    }
    public DbObject getLog(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LOG, new String[]{LOG_ID,
                        LOG_TIME, LOG_BAT_LVL, LOG_TEMP, LOG_CURRENT, LOG_SOT, LOG_SCREEN_OFF_CURRENT, LOG_SCREEN_ON_CURRENT,LOG_SCREEN_STATE}, LOG_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        DbObject dbObject;
        assert cursor != null;
        dbObject = new DbObject(Integer.parseInt(cursor.getString(0)), cursor.getLong(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5),
                cursor.getInt(6), cursor.getInt(7),cursor.getInt(8)>0);

        cursor.close();
        db.close();
        return dbObject;
    }
    public ArrayList<DbObject> getAllLog() {
        ArrayList<DbObject> dbArray = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOG;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DbObject dbObject = new DbObject();
                dbObject.setLogId(Integer.parseInt(cursor.getString(0)));
                dbObject.setTime(cursor.getLong(1));
                dbObject.setBatLvl(cursor.getInt(2));
                dbObject.setTemp(cursor.getInt(3));
                dbObject.setCurrent(cursor.getInt(4));
                dbObject.setSot(cursor.getInt(5));
                dbObject.setScrOffCurr(cursor.getInt(6));
                dbObject.setScrOnCurr(cursor.getInt(7));
                dbObject.setScreenState(cursor.getInt(8)>0);
                dbArray.add(dbObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dbArray;
    }
    public void migrateTimeToSeconds() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE log");
        db.close();
    }
    public void updateLog(DbObject dbObject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOG_TIME, dbObject.getTime());
        values.put(LOG_BAT_LVL, dbObject.getBatLvl()); // Contact Phone
        values.put(LOG_TEMP,dbObject.getTemp());
        values.put(LOG_CURRENT,dbObject.getCurrent());
        values.put(LOG_SOT,dbObject.getSot());
        values.put(LOG_SCREEN_OFF_CURRENT,dbObject.getScrOffCurr());
        values.put(LOG_SCREEN_ON_CURRENT,dbObject.getScrOnCurr());
        values.put(LOG_SCREEN_STATE,dbObject.getScreenState());
        db.update(TABLE_LOG, values, LOG_ID + " = ?",
                new String[]{String.valueOf(dbObject.getlogId())});
        db.close();
    }
    public int getLogCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public ArrayList<DbObject> getLogFrom(int from,int to) {
        ArrayList<DbObject> dbArray = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOG + " WHERE " + LOG_ID + " BETWEEN " + from + " AND " + to;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DbObject dbObject = new DbObject();
                dbObject.setLogId(Integer.parseInt(cursor.getString(0)));
                dbObject.setTime(cursor.getLong(1));
                dbObject.setBatLvl(cursor.getInt(2));
                dbObject.setTemp(cursor.getInt(3));
                dbObject.setCurrent(cursor.getInt(4));
                dbObject.setSot(cursor.getInt(5));
                dbObject.setScrOffCurr(cursor.getInt(6));
                dbObject.setScrOnCurr(cursor.getInt(7));
                dbObject.setScreenState(cursor.getInt(8)>0);
                dbArray.add(dbObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dbArray;
    }
    public ArrayList<DbObject> get7Days() {
        ArrayList<DbObject> dbArray = new ArrayList<>();
        long TIME = System.currentTimeMillis()/1000 - 60*60*24*7;
        String selectQuery = "SELECT  * FROM " + TABLE_LOG + " WHERE " + LOG_TIME + " > " + TIME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int batLevel = cursor.getInt(2);
                if (batLevel == -1) continue;
                DbObject dbObject = new DbObject();
                dbObject.setLogId(Integer.parseInt(cursor.getString(0)));
                dbObject.setTime(cursor.getLong(1) * 1000);
                dbObject.setBatLvl(batLevel);
                dbObject.setTemp(cursor.getInt(3));
                dbObject.setCurrent(cursor.getInt(4));
                dbObject.setSot(cursor.getInt(5));
                dbObject.setScrOffCurr(cursor.getInt(6));
                dbObject.setScrOnCurr(cursor.getInt(7));
                dbObject.setScreenState(cursor.getInt(8)>0);
                dbArray.add(dbObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dbArray;
    }

    void addRom(DbObject dbObject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ROM_ID,dbObject.getRomId());
        values.put(ROM_TIME,dbObject.getRomTime());
        values.put(ROM_KERNEL, dbObject.getRomKernel());
        values.put(ROM_NAME, dbObject.getRomName());
        values.put(ROM_COUNT, dbObject.getRomCount());
        values.put(ROM_PROP, dbObject.getRomProp());
        db.insert(TABLE_ROM, null, values);
        db.close();
    }
    public DbObject getRom(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROM, new String[]{ROM_ID,
                ROM_TIME, ROM_KERNEL,ROM_NAME,ROM_COUNT,ROM_PROP}, ROM_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        DbObject dbObject = new DbObject();
        if (cursor != null && cursor.moveToFirst()) {
            dbObject = new DbObject(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5));
            cursor.close();
        }
        db.close();
        return dbObject;
    }
    public void updateRom(int count) {
        DbObject dbO = getRom(getRomCount());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ROM_TIME, dbO.getRomTime());
        values.put(ROM_KERNEL, dbO.getRomKernel());
        values.put(ROM_NAME, dbO.getRomName());
        values.put(ROM_COUNT, count);
        values.put(ROM_PROP,dbO.getRomProp());
        db.update(TABLE_ROM, values, ROM_ID + " = ?", new String[]{String.valueOf(dbO.getRomId())});
        db.close();
    }
    public void updaterRom(int from,int to) {
        DbObject dbfrom = getRom(from);

        SQLiteDatabase db = this.getWritableDatabase();
        dbfrom.setRomId(to);
        addRom(dbfrom);
        deleteRom(from);
        db.close();
    }
    public void deleteRom(int d1) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROM, "id=?", new String[]{String.valueOf(d1)});
    }

    public ArrayList<DbObject> getAllRom() {
        ArrayList<DbObject> dbArray = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_ROM;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DbObject dbObject = new DbObject();
                dbObject.setRomId(cursor.getInt(0));
                dbObject.setRomTime(cursor.getInt(1));
                dbObject.setRomKernel(cursor.getString(2));
                dbObject.setRomName(cursor.getString(3));
                dbObject.setRomCount(cursor.getInt(4));
                dbObject.setRomProp(cursor.getString(5));
                dbArray.add(dbObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dbArray;
    }
    public int getRomCount() {
        String countQuery = "SELECT  "+ROM_ID+" FROM " + TABLE_ROM;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount() - 1 ;
        cursor.close();
        db.close();
        return count;
    }

}