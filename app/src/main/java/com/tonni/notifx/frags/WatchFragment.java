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
import com.tonni.notifx.Utils.SortPending;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.inter.MainActivityInterface;
import com.tonni.notifx.inter.PendingInterface;
import com.tonni.notifx.adapter.Watch_list_Adapter;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.ForexCurrency;
import com.tonni.notifx.models.PendingPrice;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WatchFragment extends Fragment implements RefreshableFragment, PendingInterface {

    private MainActivityInterface mainActivityInterface;
    private RecyclerView recyclerView;
    private Watch_list_Adapter pendingAdapter;
    private static final String FILE_NAME_PENDING = "pending.json";
    private ArrayList<PendingPrice> pendingPrices = new ArrayList<>();
    private static final String FILE_NAME_PENDING_LOCAL = "pending.json";
    private static final String FILE_NAME_CURRENCIES_LOCAL = "currencies.json";


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

        pendingAdapter = new Watch_list_Adapter(this, pendingPrices,getContext());
        recyclerView.setAdapter(pendingAdapter);

       getLocalFile(pendingPrices);
        return view;
    }


    @Override
    public void refresh() {
        Log.d("PendingFragment", "Refreshing data...");

    }


    public void deletePendingPos( int pos){
        int realPosOfCurrency=pendingPrices.get(pos).getPosFromCurrency();
        pendingPrices.remove(pos);
        pendingAdapter.notifyItemRemoved(pos);

        //update forex fragment
        try {
            mainActivityInterface.UpdateForexMainActivity().refreshUIFromPending_remove(pos,realPosOfCurrency);
        }catch (Exception e){
            save_file_forex_currencies(realPosOfCurrency);
        }
        save_files();
    }

    public void add_chain_to_master(int pos){
        int realPosOfCurrency=pendingPrices.get(pos).getPosFromCurrency();


        mainActivityInterface.UpdateForexMainActivity().refreshUIFromPending_add(pos,realPosOfCurrency,pos);
    }

    private void save_file_forex_currencies(int real_currency_pos) {
        // Load  items from JSON
        String readJsonData2 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_CURRENCIES_LOCAL);
        Log.d("CURRENCIES_FOREX_LOCAL",readJsonData2 );


        // Parse JSON data
        Type listType1 = new TypeToken<List<ForexCurrency>>() {}.getType();
        ArrayList<ForexCurrency> currencies=new Gson().fromJson(readJsonData2, listType1);

        //Set the int
        ForexCurrency forexCurrency = currencies.get(real_currency_pos);
        forexCurrency.setAlertNumber( forexCurrency.getAlertNumber()-1);
        currencies.set(real_currency_pos,forexCurrency);

        //save new file

        // save to local
        Gson gson = new Gson();
        String jsonData = gson.toJson(currencies);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_CURRENCIES_LOCAL, jsonData);





    }


    @Override
    public void UpdateUI() {

    }

    @Override
    public void refreshUIFromForex(PendingPrice pendingPrice) {
        //Add the item to the list and update the adapter
        int pos=pendingPrices.size();
        pendingPrices.add(pendingPrice);

        //       TODO To remove
        ArrayList<PendingPrice> temp = new ArrayList<>();
        temp.addAll(pendingPrices);
        pendingPrices.clear();
        pendingPrices.addAll(SortPending.sort(temp));
        //        TODO To remove up
//        pendingAdapter.notifyItemInserted(pos);
        pendingAdapter.notifyDataSetChanged();
        save_file();
    }

    @Override
    public void addLongIdToMaster(int master_pos, long id) {
        ArrayList<Long> temp_list=pendingPrices.get(master_pos).getChainlist();


        if (temp_list==null){
            temp_list=new ArrayList<Long>();
            temp_list.add(id);
        }else {
            temp_list.add(id);
        }

        pendingPrices.get(master_pos).setChainlist(temp_list);
        pendingPrices.get(master_pos).setIsChainActive(1);
    }


    public boolean getLocalFile(ArrayList<PendingPrice> pendingPricesForex){
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_PENDING_LOCAL);
        Log.d("WATCH-DATA-CHG", readJsonData1);
        // Parse JSON data
        Type listType2 = new TypeToken<List<PendingPrice>>() {}.getType();
        ArrayList<PendingPrice> pendingPrices_=new Gson().fromJson(readJsonData1, listType2);

//
//        for (int i = 0; i < pendingPrices_.size(); i++) {
//            pendingPrices_.get(i).setIs_chain_Pending(0);
//        }

        if(pendingPrices_==null){
            pendingPrices_=new ArrayList<PendingPrice>();
            pendingPricesForex=new ArrayList<>();
            save_files();
        }

        pendingPricesForex.addAll(pendingPrices_);
        //        TODO To remove
        ArrayList<PendingPrice> temp = new ArrayList<>();
        temp.addAll(pendingPricesForex);
        pendingPricesForex.clear();
        pendingPricesForex.addAll(SortPending.sort(temp));


        //        TODO To remove up
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

    public  void save_file(){
        Gson gson = new Gson();
        //CLEAR
        ArrayList<PendingPrice> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData_);

        // SAVE DATA
        String jsonData = gson.toJson(pendingPrices);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData);

    }


    public  void save_files(){
        Gson gson = new Gson();
        //CLEAR
        ArrayList<PendingPrice> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
//        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData_);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData_);

        // SAVE DATA
        String jsonData = gson.toJson(pendingPrices);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData);
//        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData);

    }



}
