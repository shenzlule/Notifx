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
import com.tonni.notifx.inter.MainActivityInterface;
import com.tonni.notifx.inter.PendingInterface;
import com.tonni.notifx.adapter.PendingAdapter;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.PendingPrice;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PendingFragment extends Fragment implements RefreshableFragment, PendingInterface {

    private MainActivityInterface mainActivityInterface;
    private RecyclerView recyclerView;
    private PendingAdapter pendingAdapter;
    private static final String FILE_NAME_PENDING = "pending.json";
    private ArrayList<PendingPrice> pendingPrices = new ArrayList<>();
    private static final String FILE_NAME_PENDING_LOCAL = "pending_pending.json";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);


        try {
            mainActivityInterface = (MainActivityInterface) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getContext().toString() + " must implement MainActivityInterface");
        }
        recyclerView = view.findViewById(R.id.recycler_view_pending);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        pendingPrices= new ArrayList<>();

        pendingAdapter = new PendingAdapter(this, pendingPrices,getContext());
        recyclerView.setAdapter(pendingAdapter);

       getLocalFile(pendingPrices);
        return view;
    }


    @Override
    public void refresh() {
        Log.d("PendingFragment", "Refreshing data...");

    }


    public void deletePendingPos( int pos){
        pendingPrices.remove(pos);
        pendingAdapter.notifyItemRemoved(pos);
        //update forex fragment
        mainActivityInterface.UpdateForexMainActivity().refreshUIFromPending(pos);
        saved_files();
    }


    @Override
    public void UpdateUI() {

    }

    @Override
    public void refreshUIFromForex(PendingPrice pendingPrice) {
        //Add the item to the list and update the adapter
        int pos=pendingPrices.size();
        pendingPrices.add(pendingPrice);
        pendingAdapter.notifyItemInserted(pos);
        saved_file();
    }



    public boolean getLocalFile(ArrayList<PendingPrice> pendingPricesForex){
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_PENDING_LOCAL);
        // Parse JSON data
        Type listType2 = new TypeToken<List<PendingPrice>>() {}.getType();
        ArrayList<PendingPrice> pendingPrices_=new Gson().fromJson(readJsonData1, listType2);



        if(pendingPrices_==null){
            pendingPrices_=new ArrayList<PendingPrice>();
            pendingPricesForex=new ArrayList<>();
        }

        pendingPricesForex.addAll(pendingPrices_);
        pendingAdapter.notifyDataSetChanged();

        return true;
    }

    public void getLocalFile_main(){
        Log.d("MainActivity-Broadcast", "broadcast pending");

        pendingPrices.clear();
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_PENDING_LOCAL);
        // Parse JSON data
        Type listType2 = new TypeToken<List<PendingPrice>>() {}.getType();
        ArrayList<PendingPrice> pendingPrices_=new Gson().fromJson(readJsonData1, listType2);



        if(pendingPrices_==null){
            pendingPrices_=new ArrayList<PendingPrice>();
        }

        pendingPrices.addAll(pendingPrices_);
        Log.d("MainActivity-Broadcast", "broadcast pending =="+String.valueOf(pendingPrices.size()));
        pendingAdapter.notifyDataSetChanged();

    }

    public  void saved_file(){
        Gson gson = new Gson();
        //CLEAR
        ArrayList<PendingPrice> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData_);

        // SAVE DATA
        String jsonData = gson.toJson(pendingPrices);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData);

    }


    public  void saved_files(){
        Gson gson = new Gson();
        //CLEAR
        ArrayList<PendingPrice> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData_);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData_);

        // SAVE DATA
        String jsonData = gson.toJson(pendingPrices);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData);

    }


}
