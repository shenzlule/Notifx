package com.tonni.notifx.frags;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.SortPending;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.adapter.ForexCurrencyAdapter;
import com.tonni.notifx.Utils.SwipeToRevealCallback;
import com.tonni.notifx.inter.ForexCurrencyInterface;
import com.tonni.notifx.inter.MainActivityInterface;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.ForexCurrency;
import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import es.dmoral.toasty.Toasty;

public class ForexFragment extends Fragment implements RefreshableFragment, ForexCurrencyInterface {

    private MainActivityInterface mainActivityInterface;
    private RecyclerView recyclerView;
    private ForexCurrencyAdapter adapter;
    private SwipeToRevealCallback swipeToRevealCallback;
    private static final String FILE_NAME = "forex_data.json";
//    private static final String FILE_NAME_PENDING_FOREX_LOCAL = "pending_forex.json";
    private static final String FILE_NAME_CURRENCIES_LOCAL = "currencies.json";
    private static final String FILE_NAME_PENDING = "pending.json";
//    private ArrayList<PendingPrice> pendingPrices;
    private ArrayList<ForexCurrency> currencies;
//    private static final String FILE_NAME_PENDING_LOCAL = "pending_pending.json";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forex, container, false);

        // create a progress bar variable and set the id
        final ProgressBar progressBar = view.findViewById(R.id.ProgressBar01);



        try {
            mainActivityInterface = (MainActivityInterface) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getContext().toString() + " must implement MainActivityInterface");
        }
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        pendingPrices=new ArrayList<>();
        currencies=new ArrayList<>();





        adapter = new ForexCurrencyAdapter(currencies,this);
        recyclerView.setAdapter(adapter);

        // show the progress bar

        if(getLocalFile(currencies))
        {
            progressBar.setVisibility(View.GONE);
        }



        return view;
    }

    public boolean showInputDialog(int position, ForexCurrency forexCurrency) {

        AtomicBoolean bol_success= new AtomicBoolean(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input, null);
        builder.setView(dialogView);

        // Get references to the UI components
        int decimalNumber =forexCurrency.getPipNumber();
        EditText price_input = dialogView.findViewById(R.id.dialog_input);
        EditText notes_input = dialogView.findViewById(R.id.dialog_note);
        Button buttonOk = dialogView.findViewById(R.id.button_ok);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);



// Add text watcher to price input to ensure it has the correct number of decimals
        price_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        RadioGroup dirGroup = dialogView.findViewById(R.id.dir);
        // Get the selected RadioButton ID from complianceGroup
        AtomicInteger dirId = new AtomicInteger(-1);



        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        buttonOk.setOnClickListener(v -> {
            String priceInputText = price_input.getText().toString().trim();
            String notesInputText = notes_input.getText().toString().trim();
            dirId.set(dirGroup.getCheckedRadioButtonId());

            if (priceInputText.isEmpty()) {
                Toast.makeText(getContext(), "Price cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dirId.get() ==-1) {
                Toast.makeText(getContext(), "Direction can not be null", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton dirRadioButton = dialogView.findViewById(dirId.get());

            String  dirText = dirRadioButton.getText().toString();
            if (dirText.equals("breakup")){
                dirText="above";
            }else if (dirText.equals("breakdown")){
                dirText="below";
            }

            if (notesInputText.isEmpty()) {
                Toast.makeText(getContext(), "Note cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }


            try {

                String formattedPriceInput=check_price_pip(priceInputText,decimalNumber);
                price_input.setText(formattedPriceInput);

                String pair_=forexCurrency.getBaseCurrency()  + forexCurrency.getQuoteCurrency();
                String pair_v=forexCurrency.getBaseCurrency() +"/" + forexCurrency.getQuoteCurrency();
                if (forexCurrency.getQuoteCurrency().equals("@")){
                    pair_=forexCurrency.getBaseCurrency();
                }
                Calendar calendar = Calendar.getInstance();
                addNewPending(position, formattedPriceInput, notesInputText,
                        pair_,
                        String.valueOf(calendar.getTimeInMillis()),dirText,pair_v,forexCurrency.getPosition());
                Toasty.info(getContext(), "Input: " + formattedPriceInput+dirText +" "+notes_input.getText().toString() , Toast.LENGTH_SHORT, true).show();
                bol_success.set(true);
            } catch (NumberFormatException e) {
//                Toast.makeText(getContext(), "Invalid price input", Toast.LENGTH_SHORT).show();
                Toasty.warning(getContext(), "Invalid price input", Toast.LENGTH_SHORT, true).show();

            }
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> {
            dialog.cancel();
            bol_success.set(false);
        });

        dialog.show();

        return bol_success.get();
    }

    public boolean showInputDialog_chain(int master_pos,int position, ForexCurrency forexCurrency,DialogCallback dialogCallback) {

        AtomicBoolean data_return = new AtomicBoolean(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());


        // Inflate the custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input, null);
        builder.setView(dialogView);

        // Get references to the UI components
        int decimalNumber =forexCurrency.getPipNumber();
        EditText price_input = dialogView.findViewById(R.id.dialog_input);
        EditText notes_input = dialogView.findViewById(R.id.dialog_note);
        Button buttonOk = dialogView.findViewById(R.id.button_ok);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);



// Add text watcher to price input to ensure it has the correct number of decimals
        price_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        RadioGroup dirGroup = dialogView.findViewById(R.id.dir);
        // Get the selected RadioButton ID from complianceGroup
        AtomicInteger dirId = new AtomicInteger(-1);



        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        buttonOk.setOnClickListener(v -> {
            String priceInputText = price_input.getText().toString().trim();
            String notesInputText = notes_input.getText().toString().trim();
            dirId.set(dirGroup.getCheckedRadioButtonId());

            if (priceInputText.isEmpty()) {
                Toast.makeText(getContext(), "Price cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dirId.get() ==-1) {
                Toast.makeText(getContext(), "Direction can not be null", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton dirRadioButton = dialogView.findViewById(dirId.get());

            String  dirText = dirRadioButton.getText().toString();
            if (dirText.equals("breakup")){
                dirText="above";
            }else if (dirText.equals("breakdown")){
                dirText="below";
            }

            if (notesInputText.isEmpty()) {
                Toast.makeText(getContext(), "Note cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }


            try {

                String formattedPriceInput=check_price_pip(priceInputText,decimalNumber);
                price_input.setText(formattedPriceInput);

                String pair_=forexCurrency.getBaseCurrency()  + forexCurrency.getQuoteCurrency();
                String pair_v=forexCurrency.getBaseCurrency() +"/" + forexCurrency.getQuoteCurrency();
                if (forexCurrency.getQuoteCurrency().equals("@")){
                    pair_=forexCurrency.getBaseCurrency();
                }
                Calendar calendar = Calendar.getInstance();
                addNewPending_chain(master_pos,position, formattedPriceInput, notesInputText,
                        pair_,
                        String.valueOf(calendar.getTimeInMillis()),dirText,pair_v,forexCurrency.getPosition());
                Toasty.info(getContext(), "Input: " + formattedPriceInput+dirText +" "+notes_input.getText().toString() , Toast.LENGTH_SHORT, true).show();

                dialogCallback.onSuccess(true);
            } catch (NumberFormatException e) {
//                Toast.makeText(getContext(), "Invalid price input", Toast.LENGTH_SHORT).show();
                Toasty.warning(getContext(), "Invalid price input", Toast.LENGTH_SHORT, true).show();

            }
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(v -> {
            data_return.set(false);
            dialog.cancel();

        });

        dialog.show();

        return data_return.get();
    }



    private void addNewPending(int position ,String price , String note,String pair,String date,String dir,String visible_lable,int realPos){
//        pendingPrices.add(new PendingPrice(price,pair,visible_lable,date,note,"Not","Null",dir,realPos));
//        setting the alert number
        ForexCurrency tempCurrency=currencies.get(position);
        tempCurrency.setAlertNumber(tempCurrency.getAlertNumber()+1);
        currencies.set(position,tempCurrency);
        adapter.notifyItemChanged(position);

        saved_file_curencies();
        try {
            mainActivityInterface.UpdatePendingMainActivity().refreshUIFromForex(new PendingPrice(price,pair,visible_lable,date,note,"Not","Null",dir,realPos));
        }catch (Exception e){
            save_file_pending(new PendingPrice(price,pair,visible_lable,date,note,"Not","Null",dir,realPos));
        }
    }


    private void addNewPending_chain(int master_pos,int position ,String price , String note,String pair,String date,String dir,String visible_lable,int realPos){

        PendingPrice temp_pending_price=new PendingPrice(price,pair,visible_lable,date,note,"Not","Null",dir,realPos);
        temp_pending_price.setIs_chain_Pending(1);

        try {
//            this should be the first to be called
            mainActivityInterface.UpdatePendingMainActivity().addLongIdToMaster(master_pos,temp_pending_price.getId());

//            the this below
            mainActivityInterface.UpdatePendingMainActivity().refreshUIFromForex(temp_pending_price);
        }catch (Exception e){
            Toasty.error(getContext(), "Error occured while trying to save chain link price item", Toast.LENGTH_SHORT, true).show();
        }
    }

    private static String  check_price_pip(String price_arg, int pip_number) {
        String price = price_arg;
        String new_price=price_arg;

        String[] split_list = price.split("[.]");

        if (price.contains(".") == true) {
            if(price.charAt(price.length()-1) != '.') {

                if (split_list[1].length() < pip_number) {
                    int num_rem_ = split_list[1].length() - 1;
                    int num_rem = split_list[1].length();
                    num_rem = pip_number - num_rem;

                    for (int i = 1; i <= num_rem; i++) {
                        split_list[1] = split_list[1] + "0";
                    }
                    new_price = split_list[0] + "." + split_list[1];
                }
            }else if(price.charAt(price.length()-1) == '.') {
                String sep = ".";
                split_list[0] = split_list[0] + sep;
                for (int i = 1; i <= pip_number; i++) {
                    split_list[0] = split_list[0] + "0";
                    System.out.println(i);
                }
                new_price = split_list[0];
            }
        } else {
            String sep = ".";
            split_list[0] = split_list[0] + sep;
            for (int i = 1; i <= pip_number; i++) {
                split_list[0] = split_list[0] + "0";
                System.out.println(i);
            }
            new_price = split_list[0];
        }
        return new_price;
    }

    @Override
    public void refresh() {

        Log.d("PendingFragment", "Refreshing data...");


    }

    @Override
    public void UpdateUI() {

    }

    @Override
    public void refreshUIFromPending_remove(int pos, int realPos) {
//        pendingPrices.remove(pos);
        //        TODO To remove
//        ArrayList<PendingPrice> temp = new ArrayList<>();
//        temp.addAll(pendingPrices);
//        pendingPrices.clear();
//        pendingPrices.addAll(SortPending.sort(temp));
        //        TODO To remove up
        ForexCurrency tempCurrency=currencies.get(realPos);
        tempCurrency.setAlertNumber(tempCurrency.getAlertNumber()-1);
        if(tempCurrency.getAlertNumber()<0){
            tempCurrency.setAlertNumber(0);

        }
        currencies.set(realPos,tempCurrency);
        adapter.notifyItemChanged(realPos);

        saved_file_curencies();
//        save_file();
    }

    @Override
    public void refreshUIFromPending_add(int pos, int realPos,int pos_for_master_chain) {


        ForexCurrency tempCurrency=currencies.get(realPos);


       showInputDialog_chain(pos ,realPos,tempCurrency, new DialogCallback() {
           @Override
           public void onSuccess(boolean success) {
               if (success) {
                   tempCurrency.setAlertNumber(tempCurrency.getAlertNumber() + 1);
//                   Toasty.success(ForexFragment.this.getContext(), "Ok currency updtaed", Toast.LENGTH_SHORT, true).show();

                   currencies.set(realPos, tempCurrency);
                   adapter.notifyItemChanged(realPos);

                   ForexFragment.this.saved_file_curencies();
               }
           }
       });




    }

    @Override
    public void backUpData() {

        new AlertDialog.Builder(requireContext())
                .setTitle("Back Data")
                .setMessage("Are you sure you want to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toasty.success(getContext(), "On it to backup Data", Toast.LENGTH_SHORT, true).show();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .show();
    }


    public boolean getLocalFile(ArrayList<ForexCurrency> forexCurrencies){
        // Load  items from JSON
//        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_PENDING_FOREX_LOCAL);
        String readJsonData2 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_CURRENCIES_LOCAL);
        Log.d("CURRENCIES_FOREX_LOCAL",readJsonData2 );


        // Parse JSON data
        Type listType1 = new TypeToken<List<ForexCurrency>>() {}.getType();
        ArrayList<ForexCurrency> currencies=new Gson().fromJson(readJsonData2, listType1);

//        Type listType2 = new TypeToken<List<PendingPrice>>() {}.getType();
//        ArrayList<PendingPrice> pendingPrices=new Gson().fromJson(readJsonData1, listType2);



        if(currencies == null){
            currencies=new ArrayList<ForexCurrency>();
            currencies.add(new ForexCurrency( "USA30", "@",  0,0,1,0));
            currencies.add(new ForexCurrency( "NAS100", "@",  0,0,2,1));
            currencies.add(new ForexCurrency( "USDX", "@",  0,0,1,2));
            currencies.add(new ForexCurrency( "XAU","USD",  0,0,3,3));
            currencies.add(new ForexCurrency("GBP", "USD",  0,0,5,4));
            currencies.add(new ForexCurrency("GBP","JPY",  0,0,3,5));
            currencies.add(new ForexCurrency("EUR", "USD",  0,0,5,6));
            currencies.add(new ForexCurrency("EUR","JPY",  0,0,3,7));
            currencies.add(new ForexCurrency( "USD","JPY",  0,0,3,8));
            currencies.add(new ForexCurrency( "USD","CAD",  0,0,5,9));
            currencies.add(new ForexCurrency("USD","CHF",  0,0,5,10));
            currencies.add(new ForexCurrency("CAD","JPY",  0,0,3,11));
            currencies.add(new ForexCurrency("CAD","CHF",  0,0,5,12));
            currencies.add(new ForexCurrency("CHF","JPY",  0,0,3,13));
            currencies.add(new ForexCurrency("NZD","CAD",  0,0,5,14));
            currencies.add(new ForexCurrency("XAG","USD",  0,0,3,15));
            currencies.add(new ForexCurrency("OIL","@",  0,0,3,16));



            // save to local
            Gson gson = new Gson();
            String jsonData = gson.toJson(currencies);
            StorageUtils.writeJsonToFile(getContext(), FILE_NAME_CURRENCIES_LOCAL, jsonData);
        }



//        if(pendingPrices == null){
//            pendingPrices=new ArrayList<PendingPrice>();
//
//            // save to local
//            Gson gson = new Gson();
//            String jsonData = gson.toJson(pendingPrices);
//            StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_FOREX_LOCAL, jsonData);
//
//        }

        forexCurrencies.addAll(currencies);
//        pendingPricesForex.addAll(pendingPrices);



        adapter.notifyDataSetChanged();

        return true;
    }



    public  void save_file_pending(PendingPrice pendingPrice){

        Gson gson = new Gson();

        String readJsonData = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_PENDING);


        Type listType2 = new TypeToken<List<PendingPrice>>() {}.getType();
        ArrayList<PendingPrice> pendingPriceslist=new Gson().fromJson(readJsonData, listType2);
        pendingPriceslist.add(pendingPrice);


        //CLEAR
        ArrayList<PendingPrice> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData_);

        //sort
        ArrayList<PendingPrice> temp = new ArrayList<>();
        temp.addAll(pendingPriceslist);
        pendingPriceslist.clear();
        pendingPriceslist.addAll(SortPending.sort(temp));


        // SAVE DATA
        String jsonData = gson.toJson(pendingPriceslist);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING, jsonData);

    }

    public  void saved_file_curencies(){
        Gson gson = new Gson();
        //CLEAR
        ArrayList<ForexCurrency> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_CURRENCIES_LOCAL, jsonData_);

        // SAVE DATA
        String jsonData = gson.toJson(currencies);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_CURRENCIES_LOCAL, jsonData);

    }


    public void getLocalFile_main(){


//        Log.d("MainActivity-Broadcast", "broadcast forex");
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_CURRENCIES_LOCAL);


        Type listType2 = new TypeToken<List<ForexCurrency>>() {}.getType();
        ArrayList<ForexCurrency> forexCurrencies=new Gson().fromJson(readJsonData1, listType2);


        if(forexCurrencies==null){
            forexCurrencies=new ArrayList<ForexCurrency>();

//            // save to local
//            Gson gson = new Gson();
//            String jsonData = gson.toJson(pendingPrices);
//            StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_FOREX_LOCAL, jsonData);

        }

        currencies.clear();

        currencies.addAll(forexCurrencies);



        Log.d("MainActivity-Broadcast", "broadcast forex =="+String.valueOf(currencies.size()));

        adapter.notifyDataSetChanged();

    }



    public interface DialogCallback{
        public  void onSuccess(boolean success);
    }

}
