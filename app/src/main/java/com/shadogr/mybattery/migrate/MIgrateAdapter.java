package com.shadogr.mybattery.migrate;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MIgrateAdapter extends RecyclerView.Adapter<MIgrateAdapter.ViewHolder> {
    ArrayList<MigrateObject> arrayList;

    public MIgrateAdapter(ArrayList<MigrateObject> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @NotNull
    @Override
    public MIgrateAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull MIgrateAdapter.ViewHolder holder, int position) {
        holder.textView.setText(arrayList.get(position).rom);
        holder.textView.setOnClickListener(v -> {
            if (arrayList.get(position).isChecked){
                arrayList.get(position).isChecked = false;
                holder.textView.setBackgroundColor(Color.WHITE);
            }
            else{
            arrayList.get(position).isChecked = true;
            holder.textView.setBackgroundColor(Color.YELLOW);}
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(android.R.id.text1);
        }
    }

}
