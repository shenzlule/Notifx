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
import com.tonni.notifx.adapter.FilledAdapter;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FilledFragment extends Fragment implements RefreshableFragment {

    private FilledAdapter filledAdapter;
    private static final String FILE_NAME_PENDING = "pending.json";
    private ArrayList<PendingPrice> filledPrices;
    private static final String FILE_NAME_FILLED_LOCAL = "filled.json";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filled, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_filled);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        filledPrices = new ArrayList<>();

        filledAdapter = new FilledAdapter(this, filledPrices,getContext());
        recyclerView.setAdapter(filledAdapter);

        getLocalFile(filledPrices); // This will now update the list and notify the adapter
        return view;
    }


    @Override
    public void refresh() {
        Log.d("FilledFragment", "Refreshing data...");


    }


    public void deleteFilledPos( int pos){
        filledPrices.remove(pos);
        filledAdapter.notifyItemRemoved(pos);
        saved_file();

    }



    public boolean getLocalFile(ArrayList<PendingPrice> filledPricesForex){
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_FILLED_LOCAL);
        // Parse JSON data
        Type listType2 = new TypeToken<List<PendingPrice>>() {}.getType();
        ArrayList<PendingPrice> filledPrices_=new Gson().fromJson(readJsonData1, listType2);



        if(filledPrices_==null){
            filledPrices_=new ArrayList<PendingPrice>();
            // save to local
            Gson gson = new Gson();
            String jsonData = gson.toJson(filledPrices_);
            StorageUtils.writeJsonToFile(getContext(), FILE_NAME_FILLED_LOCAL, jsonData);
        }

        filledPricesForex.addAll(filledPrices_);
        filledAdapter.notifyDataSetChanged();

        return true;
    }
    public boolean getLocalFile_main(){
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_FILLED_LOCAL);
        // Parse JSON data
        Type listType2 = new TypeToken<List<PendingPrice>>() {}.getType();
        ArrayList<PendingPrice> filledPrices_=new Gson().fromJson(readJsonData1, listType2);



        if(filledPrices_==null){
            filledPrices_=new ArrayList<PendingPrice>();
            // save to local
        }

        filledPrices.clear();
        filledPrices.addAll(filledPrices_);
        filledAdapter.notifyDataSetChanged();

        return true;
    }

    public  void saved_file(){
        Gson gson = new Gson();
        //CLEAR
        ArrayList<PendingPrice> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_FILLED_LOCAL, jsonData_);

        // SAVE DATA
        String jsonData = gson.toJson(filledPrices);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_FILLED_LOCAL, jsonData);

    }





}
