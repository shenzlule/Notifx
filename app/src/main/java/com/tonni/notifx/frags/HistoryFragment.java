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
import com.tonni.notifx.Utils.SwipeToRevealCallback;
import com.tonni.notifx.adapter.HistoryAdapter;
import com.tonni.notifx.inter.MainActivityInterface;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.ForexCurrency;
import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements RefreshableFragment {

    private MainActivityInterface mainActivityInterface;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private SwipeToRevealCallback swipeToRevealCallback;
    private static final String FILE_NAME = "forex_data.json";
    private static final String FILE_NAME_PENDING = "pending.json";
    private List<PendingPrice> pendingPrices;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forex, container, false);

        try {
            mainActivityInterface = (MainActivityInterface) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getContext().toString() + " must implement MainActivityInterface");
        }
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ForexCurrency> forexCurrencyList = new ArrayList<>();
        forexCurrencyList.add(new ForexCurrency("GBP", "USD", 1.29821,0,5));
        forexCurrencyList.add(new ForexCurrency("EUR", "USD", 1.1800,0,5));
        forexCurrencyList.add(new ForexCurrency( "USD","JPY", 0.0094,0,3));
        forexCurrencyList.add(new ForexCurrency( "USA30", "@",0.0094,0,1));
        forexCurrencyList.add(new ForexCurrency( "XAU","USD", 0.0094,0,3));
        forexCurrencyList.add(new ForexCurrency( "USD","CAD", 0.0094,0,5));
        forexCurrencyList.add(new ForexCurrency("USD","CHF",  0.0094,0,5));

        readPending();

        adapter = new HistoryAdapter(forexCurrencyList,this,pendingPrices);
        recyclerView.setAdapter(adapter);
//        swipeToRevealCallback=new SwipeToRevealCallback(adapter,this,);

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToRevealCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

//        // Example JSON data
//        String jsonData = "{\"currency\":\"USD\",\"rate\":1.25}";
//
//        // Write JSON data to internal storage
//        StorageUtils.writeJsonToFile(requireContext(), FILE_NAME, jsonData);

        // Read JSON data from internal storage
        String readJsonData = StorageUtils.readJsonFromFile(requireContext(), FILE_NAME);
//        Toast.makeText(getContext(), String.valueOf("ForexFragment"+" Read JSON: " + readJsonData), Toast.LENGTH_SHORT).show();

        return view;
    }

    public void showInputDialog(int position, ForexCurrency forexCurrency) {

    }



    private void readPending(){

        // Load Forex news items from JSON
        String readJsonData = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_PENDING);
//        Toast.makeText(getContext(), "Read JSON: " + readJsonData, Toast.LENGTH_SHORT).show();

        // Parse JSON data
        Type listType = new TypeToken<List<PendingPrice>>() {}.getType();
        pendingPrices = new Gson().fromJson(readJsonData, listType);

        if (pendingPrices == null) {
            pendingPrices = new ArrayList<>();
        }
    }



    @Override
    public void refresh() {

//        mainActivityInterface.MakeConnThruInter();
//        adapter.notifyDataSetChanged();
    }
}
