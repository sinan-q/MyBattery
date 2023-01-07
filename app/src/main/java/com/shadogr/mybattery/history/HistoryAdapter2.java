package com.shadogr.mybattery.history;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shadogr.mybattery.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HistoryAdapter2 extends RecyclerView.Adapter<HistoryAdapter2.ViewHolder> {
    private final ArrayList<HistoryDataModel> arrayList;
    private final Context context;

    public HistoryAdapter2(ArrayList<HistoryDataModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public HistoryAdapter2.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_rvlayout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryAdapter2.ViewHolder holder, int position) {
        HistoryDataModel dataModel = arrayList.get(position);
        holder.test.setText(dataModel.RomTest + "");
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context,HistoryOnClickView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("ids",dataModel.ids);
            intent.putExtra("temp", dataModel.temp);
            context.startActivity(intent);
        });}

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView test;
        CardView cardView,cardView2;

        public ViewHolder(View v) {
            super(v);
            test = v.findViewById(R.id.history_rom_name);
            cardView = v.findViewById(R.id.history_cardview);
            cardView2 = v.findViewById(R.id.history_rom_cardview);
            cardView.setVisibility(View.GONE);
            cardView2.setVisibility(View.VISIBLE);
        }
    }

}
