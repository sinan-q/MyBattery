package com.shadogr.mybattery.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shadogr.mybattery.DatabaseHandler;
import com.shadogr.mybattery.DbObject;
import com.shadogr.mybattery.R;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryFragment extends Fragment {
    private final ArrayList<HistoryDataModel> arrayList = new ArrayList<>();
    private final ArrayList<HistoryDataModel> arrayListRom = new ArrayList<>();
    private HistoryAdapter adapter;
    private int i, j = 0, startBat, ScrnOnTime, ScrnOnPerc, size;
    private int tempMax, dataBatLevel, k = 0, tempMin;
    private long startTime, dataTime;
    private DbObject data;int date=0;
    private ArrayList<DbObject> onWeekLog;
    private ArrayList<DbObject> romLog;
    private RecyclerView recyclerView;
    HistoryAdapter2 adapter2;
    int test = 0;
    private static final String TAG = "Cannot invoke method length() on null object";

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        DatabaseHandler db = new DatabaseHandler(requireContext());
        initValues();
        onWeekLog = db.get7Days();

        size = onWeekLog.size();
        if (size > 2) {
            new Thread(() -> {
                date = getDate(onWeekLog.get(0).getTime());
                for (i = 0; i < size; i++) initLogic();
                if (startBat != dataBatLevel) setArrayList();
                int romSize = db.getRomCount();
                int romCountFrom = 0;
                int romCountTo;
                romLog = db.getAllLog();
                for (int t = 0 ; t<= romSize ; t++){
                    test = 0;
                    romCountTo = db.getRom(t).getRomCount();
                    for (int j=romCountFrom;j<romCountTo;j++) {
                        test++;
                    }
                    if (test!=0) {
                        arrayListRom.add(new HistoryDataModel(test,romCountFrom,romCountTo));
                        requireActivity().runOnUiThread(() -> adapter2.notifyItemChanged(arrayListRom.size()));
                    }
                    romCountFrom = romCountTo;

                }
            }).start();

            adapter = new HistoryAdapter(arrayList, requireContext());
            adapter2 = new HistoryAdapter2(arrayListRom, requireContext());
            recyclerView = view.findViewById(R.id.accHisRV);
            Log.d(TAG, "onCreateView: "+ arrayListRom.size() );
            recyclerView.setAdapter(adapter);
            LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true);
            recyclerView.setLayoutManager(manager);
        }
        return view;
    }
    private int getDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.DATE);
    }

    private void initLogic() {
        data = onWeekLog.get(i);
        int dataCurrent1 = 0;
        if (i < size - 1) dataCurrent1 = onWeekLog.get(i + 1).getCurrent();
        dataTime = data.getTime();
        int dataTemp = data.getTemp();
        dataBatLevel = data.getBatLvl();
        if (data.getScreenState()) ScrnOnPerc++;
        ScrnOnTime += data.getSot();
        if (tempMax < dataTemp) tempMax = dataTemp;
        if (tempMin > dataTemp) tempMin = dataTemp;
        boolean isCharging = data.getCurrent() < 0;
        boolean isChargingNext = dataCurrent1 < 0;
        if (i == 0) {
            startBat = dataBatLevel;
            startTime = dataTime;
        } else if (isChargingNext != isCharging && i > k && startBat != dataBatLevel) {
            if (isCharging) {
                if (i < size - 1)
                    for (k = i; k < size; k++) if (onWeekLog.get(k).getBatLvl() != dataBatLevel) break;
                if (k != size && dataBatLevel > onWeekLog.get(k).getBatLvl() && dataTime - onWeekLog.get(k).getTime() < 61)
                    setArrayList();
            } else setArrayList();
        }
    }

    private void setArrayList() {
        int newDate = getDate(dataTime);
        boolean boo = date != newDate;
        arrayList.add(new HistoryDataModel(startBat, startBat = dataBatLevel, startTime, startTime = dataTime, ScrnOnTime, (float) tempMin / 10, (float) tempMax / 10, onWeekLog.get(j).getlogId(), data.getlogId(), ScrnOnPerc,boo));
        requireActivity().runOnUiThread(() -> adapter.notifyItemChanged(arrayList.size()));
        j = i + 1;
        date = newDate;
        initValues();
    }

    @Override
    public void onDestroy() {
        recyclerView.setAdapter(null);
        arrayList.clear();
        super.onDestroy();
    }

    private void initValues() {
        tempMax = 0;
        ScrnOnTime = 0;
        ScrnOnPerc = 0;
        tempMin = 1000;
    }

}