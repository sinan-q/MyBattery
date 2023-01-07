package com.shadogr.mybattery.history;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.shadogr.mybattery.DatabaseHandler;
import com.shadogr.mybattery.LogAdapter;
import com.shadogr.mybattery.LogDataModel;
import com.shadogr.mybattery.R;
import com.shadogr.mybattery.databinding.HistoryOnclickViewBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HistoryOnClickView extends AppCompatActivity {
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int[] data = getIntent().getExtras().getIntArray("ids");
        float[] temp = getIntent().getExtras().getFloatArray("temp");
        com.shadogr.mybattery.databinding.HistoryOnclickViewBinding binding = HistoryOnclickViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ArrayList<LogDataModel> arrayList = new ArrayList<>();

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        String title = data[2]+"% to "+data[3]+"%";
        toolBarLayout.setTitle(title);
        i = data[0];
        if (i!=0) i--;
        DatabaseHandler db = new DatabaseHandler(this);
        for (;i <= data[1]; i++) arrayList.add(new LogDataModel(db.getLog(i)));
        LogAdapter adapter = new LogAdapter(arrayList, getBaseContext());
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(),LinearLayoutManager.VERTICAL,true));

        TextView lt = binding.lt;
        TextView lb = binding.lb;

        lb.setText(String.valueOf(temp[0]));
        lt.setText(FormatMillis(data[4]));

    }

    private String FormatMillis(int timeInMillis) {
        Date d = new Date(timeInMillis);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }
}