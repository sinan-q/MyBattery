package com.shadogr.mybattery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class AppUsageDBHandler extends SQLiteOpenHelper {
    private final String TABLE = "AppUsage";
    private final String USAGE_ID = "package_name";
    private final String USAGE_NAME = "app_name";
    private final String COMMA = ",";


    public AppUsageDBHandler(Context context) {
        super(context, "BatteryLog.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + "("
                + USAGE_ID + " TEXT PRIMARY KEY,"
                + USAGE_NAME + " TEXT" + ")";

        db.execSQL(CREATE_USAGE_TABLE);
        db.close();
    }

    public void createTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_USAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE + "("
                + USAGE_ID + " TEXT PRIMARY KEY,"
                + USAGE_NAME + " TEXT" + ")";

        db.execSQL(CREATE_USAGE_TABLE);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    String stringify(int string){
        return "\"" + string + "\"";
    }
    StringBuilder stringifyPro(ArrayList<Integer> string){
        StringBuilder sb= new StringBuilder();
       for (Integer i:string) {
           sb.append(",").append("\"").append(i).append("\"");
       }
       return sb;
    }

    public void renameRomId() {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i=0;i!=42;i++) {
            try {
                db.execSQL("ALTER TABLE " + TABLE + " RENAME COLUMN " + "a"+i + " TO " + stringify(i));
            } catch (IllegalStateException ignored) {
            }
        }
        db.close();
    }
    void addUsage(DbObject dbObject, int column) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USAGE_ID, dbObject.getUsageId());
        values.put(USAGE_NAME, dbObject.getUsageName());
        values.put(stringify(column), dbObject.getUsageCurrent() + "," + dbObject.getUsageCurrentCount());
        db.insert(TABLE, null, values);
        db.close();
    }

    public void addRom(int romID) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " +stringify(romID)+" TEXT");
        } catch (IllegalStateException ignored) {
        }
    }
    public void migrate() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS temps("
                + USAGE_ID + " TEXT PRIMARY KEY,"
                + USAGE_NAME + " TEXT" + ")");
        db.execSQL("INSERT INTO temps(package_name,app_name) SELECT package_name, app_name FROM AppUsage");
        db.close();
    }
    public void Migrate() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE tempk RENAME TO AppUsage");
        db.close();


    }

    public DbObject getUsage(String id, int column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE, new String[]{USAGE_ID,
                USAGE_NAME, stringify(column)}, USAGE_ID + "=?", new String[]{id}, null, null, null, null);

        if (cursor.getCount() != 0) cursor.moveToFirst();
        else return null;
        db.close();
        DbObject dbObject;
        String value = cursor.getString(2);
        String[] values;
        if (value == null) values = new String[]{"0", "1"};
        else values = cursor.getString(2).split(",");
        dbObject = new DbObject(cursor.getString(0), cursor.getString(1),
                Integer.parseInt(values[0]), Integer.parseInt(values[1]));
        cursor.close();
        return dbObject;

        /*DbObject dbObject;
        dbObject = new DbObject(cursor.getString(0), cursor.getString(1));
        cursor.close();
        return dbObject;*/
    }

    public void updateUsage(DbObject dbObject, int column) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USAGE_NAME, dbObject.getUsageName());
        values.put(stringify(column), dbObject.getUsageCurrent() + "," + dbObject.getUsageCurrentCount());
        db.update(TABLE, values, USAGE_ID + " = ?",
                new String[]{dbObject.getUsageId()});
        db.close();
    }

    public ArrayList<DbObject> getNonNullUsage(int column) {
        String[] values;
        ArrayList<DbObject> dbArray = new ArrayList<>();
        String selectQuery = "SELECT " + USAGE_ID + COMMA + USAGE_NAME + COMMA + stringify(column) + " FROM " + TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String value = cursor.getString(2);
                if (value != null) values = value.split(COMMA);
                else continue;
                DbObject dbObject = new DbObject();
                dbObject.setUsageId(cursor.getString(0));
                dbObject.setUsageName(cursor.getString(1));

                dbObject.setUsageCurrent(Integer.parseInt(values[0]));
                Log.d("TAG", "getNonNullUsage: "+value);
                dbObject.setUsageCurrentCount(Integer.parseInt(values[1]));
                dbArray.add(dbObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dbArray;
    }

    public boolean isEmpty(String column) {
        String countQuery = "SELECT  " + column + " FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        boolean boo = true;
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            do if (cursor.getString(0) != null) boo = false;
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return boo;
    }

    public ArrayList<String> getTablesNames() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> arrTblNames = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                arrTblNames.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        c.close();
        db.close();
        return arrTblNames;
    }


}
