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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final ArrayList<HistoryDataModel> arrayList;
    private final Context context;

    public HistoryAdapter(ArrayList<HistoryDataModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_rvlayout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryAdapter.ViewHolder holder, int position) {
        HistoryDataModel dataModel = arrayList.get(position);
        if (dataModel.time!=null) {
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(dataModel.time);
        }
        holder.lM.setText(dataModel.lM);
        holder.rM.setText(dataModel.rM);
        holder.rM.setTextColor(context.getResources().getColor(dataModel.rMColor, context.getTheme()));
        holder.rT.setText(dataModel.rT);
        holder.lT.setText(dataModel.lT);
        holder.lB.setText(dataModel.lB);
        holder.rB.setText(dataModel.rB);
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
        TextView lT, lM, lB, rT, rM, rB,date;
        CardView cardView;

        public ViewHolder(View v) {
            super(v);
            date = v.findViewById(R.id.dateTV);
            lT = v.findViewById(R.id.accHisItemLeftTop);
            lM = v.findViewById(R.id.accHisItemLeftMiddle);
            lB = v.findViewById(R.id.accHisItemLeftBottom);
            rT = v.findViewById(R.id.accHisItemRightTop);
            rM = v.findViewById(R.id.accHisItemRightMiddle);
            rB = v.findViewById(R.id.accHisItemRightBottom);
            cardView = v.findViewById(R.id.history_cardview);
        }
    }

}
