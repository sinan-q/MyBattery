package com.shadogr.mybattery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    private final ArrayList<LogDataModel> adapterValues;
    private final Context adapterContext;
    public LogAdapter(ArrayList<LogDataModel> values, Context context ){
        adapterValues = values;
        adapterContext = context;
    }
    @NonNull
    @Override
    public LogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(adapterContext).inflate(R.layout.log_rvlayout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.ViewHolder holder, int position) {
        LogDataModel dataModel = adapterValues.get(position);
        holder.timeView.setText(dataModel.time);
        holder.levelView.setText(dataModel.level);
        holder.tempView.setText(dataModel.temp);
        holder.tempView.setTextColor(adapterContext.getResources().getColor(dataModel.tempColor, adapterContext.getTheme()));
        holder.currentView.setText(dataModel.current);
        holder.statusView.setText(dataModel.status);
        holder.currentView.setTextColor(adapterContext.getResources().getColor(dataModel.statusColor, adapterContext.getTheme()));
        holder.durationView.setText(dataModel.duration);
    }

    @Override
    public int getItemCount() {
        return adapterValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeView,levelView,tempView,currentView,statusView,durationView;
        public ViewHolder(View v){
            super(v);
            timeView = v.findViewById(R.id.time);
            levelView = v.findViewById(R.id.level);
            tempView = v.findViewById(R.id.temp);
            currentView = v.findViewById(R.id.current);
            statusView = v.findViewById(R.id.state);
            durationView = v.findViewById(R.id.duration);
        }
    }
}
