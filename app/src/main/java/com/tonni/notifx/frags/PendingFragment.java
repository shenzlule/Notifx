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
import com.tonni.notifx.adapter.PendingAdapter;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.PendingPrice;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PendingFragment extends Fragment implements RefreshableFragment {

    private RecyclerView recyclerView;
    private PendingAdapter pendingAdapter;
    private static final String FILE_NAME_PENDING = "pending.json";
    private List<PendingPrice> pendingPrices = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_pending);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pendingAdapter = new PendingAdapter(this, pendingPrices,getContext());
        recyclerView.setAdapter(pendingAdapter);

        readPending(); // This will now update the list and notify the adapter
        return view;
    }

    private void readPending() {
        // Load Forex news items from JSON
        String readJsonData = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_PENDING);
        Log.d("PendingFragment", "Read JSON: " + readJsonData);

        // Parse JSON data
        Type listType = new TypeToken<List<PendingPrice>>() {}.getType();
        List<PendingPrice> loadedPendingPrices = new Gson().fromJson(readJsonData, listType);

        if (loadedPendingPrices == null) {
            loadedPendingPrices = new ArrayList<>();
        }

        pendingPrices.clear();
        pendingPrices.addAll(loadedPendingPrices);
        pendingAdapter.notifyDataSetChanged();
    }

    @Override
    public void refresh() {
        Log.d("PendingFragment", "Refreshing data...");


        if (TrackRefresh.getPendingFrag()==0){
            TrackRefresh.setPendingFrag(1);
        }else {
           readPending(); // This method already clears the list and notifies the adapter

        }
    }

    public void deleteVideo(PendingPrice pendingPrice,int pos){
        pendingPrices.remove(pos);
        pendingAdapter.notifyItemRemoved(pos);
        addNewPending();
    }


    private void addNewPending(){
        Gson gson = new Gson();
        List<PendingPrice> pendingPricesClear=new ArrayList<>();
        String jsonData_ = gson.toJson(pendingPricesClear);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData_);

//        forexNewsItems=null;
        String jsonData = gson.toJson(pendingPrices);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData);

    }
}
