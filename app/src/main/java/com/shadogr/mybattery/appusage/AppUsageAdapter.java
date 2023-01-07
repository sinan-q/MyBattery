package com.shadogr.mybattery.appusage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shadogr.mybattery.R;

import java.util.ArrayList;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolder> {

    public ArrayList<AppUsageDataModel> adapterArrayList;


    public AppUsageAdapter(ArrayList<AppUsageDataModel> arrayList){
        adapterArrayList = arrayList;

    }
    @NonNull
    @Override
    public AppUsageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appusage_rvlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppUsageAdapter.ViewHolder holder, int position) {
        AppUsageDataModel dataModel = adapterArrayList.get(position);
        holder.currentTV.setText(dataModel.currentText);
        holder.appIconIV.setImageBitmap(dataModel.icon);
        holder.appNameTV.setText(dataModel.appName);
    }

    @Override
    public int getItemCount() {
        return adapterArrayList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView currentTV,appNameTV;
        ImageView appIconIV;
        public ViewHolder(View itemView) {
            super(itemView);
            currentTV = itemView.findViewById(R.id.appusage_current_textview);
            appIconIV = itemView.findViewById(R.id.appusage_imageview);
            appNameTV = itemView.findViewById(R.id.appusage_appname_textview);
        }
    }
}
