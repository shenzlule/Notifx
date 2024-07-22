package com.tonni.notifx.frags;

import android.os.Bundle;
import android.util.Log;
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
import com.tonni.notifx.models.TrackRefresh;
import com.tonni.notifx.adapter.FilledAdapter;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FilledFragment extends Fragment implements RefreshableFragment {

    private FilledAdapter filledAdapter;
    private static final String FILE_NAME_PENDING = "pending.json";
    private List<PendingPrice> filledPrices;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filled, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_filled);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        filledPrices = new ArrayList<>();

        filledAdapter = new FilledAdapter(this, filledPrices,getContext());
        recyclerView.setAdapter(filledAdapter);

        readPending(); // This will now update the list and notify the adapter
        return view;
    }

    private void readPending() {
        // Load Forex news items from JSON
        String readJsonData = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_PENDING);
        Log.d("FilledFragment", "Read JSON: " + readJsonData);

        // Parse JSON data
        Type listType = new TypeToken<List<PendingPrice>>() {}.getType();
        List<PendingPrice> loadedFillPrices = new Gson().fromJson(readJsonData, listType);
        List<PendingPrice> loadedFillPrices_new=new ArrayList<>();


        if (loadedFillPrices == null) {
            loadedFillPrices = new ArrayList<>();
        }else {
            for (int i = 0; i < loadedFillPrices.size(); i++) {
                if (!loadedFillPrices.get(i).getFilled().equals("Not") ) {
                    loadedFillPrices_new.add(loadedFillPrices.get(i));
                }
            }

        }





        filledPrices.clear();
        filledPrices.addAll(loadedFillPrices_new);
        filledAdapter.notifyDataSetChanged();
    }

    @Override
    public void refresh() {
        Log.d("FilledFragment", "Refreshing data...");


        if (TrackRefresh.getFilledFrag()==0){
            TrackRefresh.setFilledFrag(1);
        }else {
           readPending(); // This method already clears the list and notifies the adapter

        }
    }


    public void deleteFilledPos(PendingPrice pendingPrice, int pos){
        filledPrices.remove(pos);
        filledAdapter.notifyItemRemoved(pos);
        addNewPending();
    }


    private void addNewPending(){
        Gson gson = new Gson();
        List<PendingPrice> pendingPricesClear=new ArrayList<>();
        String jsonData_ = gson.toJson(pendingPricesClear);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData_);

//        forexNewsItems=null;
        String jsonData = gson.toJson(filledAdapter);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData);

    }
}
