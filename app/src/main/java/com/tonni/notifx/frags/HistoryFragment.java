package com.tonni.notifx.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.adapter.HistoryAdapter;
import com.tonni.notifx.inter.MainActivityInterface;
import com.tonni.notifx.models.ForexCurrency;
import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {


    private MainActivityInterface mainActivityInterface;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private static final String FILE_NAME_HISTORY_LOCAL = "history.json";
    ArrayList<ForexCurrency> historyPips;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        try {
            mainActivityInterface = (MainActivityInterface) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getContext().toString() + " must implement MainActivityInterface");
        }
        recyclerView = view.findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        historyPips = new ArrayList<>();

        adapter = new HistoryAdapter(historyPips,this);
        recyclerView.setAdapter(adapter);

        getLocalFile(historyPips);


        return view;
    }


    public boolean getLocalFile(ArrayList<ForexCurrency> pendingPricesForex){
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_HISTORY_LOCAL);
        // Parse JSON data
        Type listType2 = new TypeToken<List<ForexCurrency>>() {}.getType();
        ArrayList<ForexCurrency> pendingPrices_=new Gson().fromJson(readJsonData1, listType2);



        if(pendingPrices_==null){
            pendingPrices_=new ArrayList<ForexCurrency>();


            // save to local
            Gson gson = new Gson();
            String jsonData = gson.toJson(pendingPrices_);
            StorageUtils.writeJsonToFile(getContext(), FILE_NAME_HISTORY_LOCAL, jsonData);
        }

        pendingPricesForex.addAll(pendingPrices_);
        adapter.notifyDataSetChanged();

        return true;
    }




}
