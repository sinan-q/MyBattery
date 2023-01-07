package com.shadogr.mybattery.migrate;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.shadogr.mybattery.AppUsageDBHandler;
import com.shadogr.mybattery.DatabaseHandler;
import com.shadogr.mybattery.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MigrateActivtiy extends AppCompatActivity {
    TextView textView;
    RecyclerView recyclerView;
    ArrayList<MigrateObject> arrayList = new ArrayList<>();
    ArrayList<MigrateObject> selected = new ArrayList<>();
    DatabaseHandler db;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migrate_activtiy);
        textView = findViewById(R.id.migrate_TV);
        recyclerView = findViewById(R.id.migrate_rv);
        button = findViewById(R.id.migrate_btn);
        DatabaseHandler db= new DatabaseHandler(this);
        AppUsageDBHandler dp = new AppUsageDBHandler(this);

        /*Log.d("TAG", "onCreate: "+db.getRomCount());
        db.updaterRom(d0,d1);
        db.deleteRom(d1);
        ArrayList<DbObject> arrayList = dp.getNonNullUsage(d0);
        for (DbObject db1:arrayList) {
            DbObject db0 = dp.getUsage(db1.getUsageId(), d1);
            int current = db0.getUsageCurrent()  + db1.getUsageCurrent() ;
            int currentCount;
            if (db0.getUsageCurrent() != 0 ) currentCount = db0.getUsageCurrentCount() + db1.getUsageCurrentCount();
            else currentCount = db1.getUsageCurrentCount();
            Log.d("TAG", "onCreate: "+db1.getUsageName() + " " + db0.getUsageName());
            Log.d("TAG", "onCreate: "+current + " " + currentCount);
            db0.setUsageCurrent(current);
            db0.setUsageCurrentCount(currentCount);
            Log.d("TAG", "onCreate: "+db0.getUsageCurrent() + " " + db0.getUsageCurrentCount());
            dp.updateUsage(db0,d0);

        }*/



        /*StringBuilder stringBuilder = new StringBuilder();
        ArrayList<DbObject> dbOArray = db.getAllRom();
        for (DbObject dbO: dbOArray) {
            Log.d("TAG", "onCreate: ");
            arrayList.add(new MigrateObject("\n"+dbO.getRomId()+" | "+dbO.getRomTime()+" | "+dbO.getRomKernel()+" | "+dbO.getRomName()+" | "+dbO.getRomCount()+" | "+dbO.getRomProp(),false)); }
        MIgrateAdapter adapter = new MIgrateAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(manager);
        button.setOnClickListener(v -> {
            selected.clear();
            for (MigrateObject object:adapter.arrayList){
                if(object.isChecked) selected.add(object);
            }
            if (selected.size() > 1) onCreateDialog().show();
        });*/

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
    public AlertDialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_MaterialComponents_DayNight);
        builder.setMessage("Confirm Delete? \n")
                .setPositiveButton("Yes", (dialog, id) -> {
                    Log.d("TAG", "onCreateDialog: yes ");
                })
                .setNegativeButton("No", (dialog, id) -> {
                    Log.d("TAG", "onCreateDialog: no ");
                });
        return builder.create();
    }
}