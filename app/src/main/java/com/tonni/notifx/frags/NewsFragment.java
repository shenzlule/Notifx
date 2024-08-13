package com.tonni.notifx.frags;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.Storage.StorageUtils;

import com.tonni.notifx.Utils.workers.NewsExpireWorker;
import com.tonni.notifx.Utils.workers.NewsWorker;
import com.tonni.notifx.adapter.ForexNewsAdapter;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.ForexNewsItem;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.tonni.notifx.models.NewsOrganised;

import es.dmoral.toasty.Toasty;

public class NewsFragment extends Fragment implements RefreshableFragment {


    private static final String TAG = "ICMP";
    private static final int REQUEST_SET_ALARM = 1;
    ArrayList<ForexNewsItem> validItems;
    private static final String FILE_NAME_NEWS_ORGANISED = "forex_events_.json";

    private static final String FILE_NAME = "forex_events.json";
    private ArrayList<ForexNewsItem> forexNewsItems;
    private ForexNewsAdapter forexNewsAdapter;
    private RecyclerView recyclerView;
    private String readJsonData;
    private Button refresh;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        refresh=view.findViewById(R.id.refresh);
        refresh.setVisibility(View.GONE);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getContext(),
                            new String[]{Manifest.permission.SET_ALARM},
                            REQUEST_SET_ALARM);
                    OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NewsWorker.class).build();
                    WorkManager.getInstance(getContext()).enqueue(workRequest);

                }else {
                    OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NewsWorker.class).build();
                    WorkManager.getInstance(getContext()).enqueue(workRequest);
                }
            }
        });




        forexNewsItems=new ArrayList<>();


        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewForexNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Set adapter
        forexNewsAdapter = new ForexNewsAdapter(forexNewsItems,getContext());
        recyclerView.setAdapter(forexNewsAdapter);


        getLocalFile(forexNewsItems);


        OneTimeWorkRequest workRequest2 = new OneTimeWorkRequest.Builder(NewsExpireWorker.class).build();
        WorkManager.getInstance(getContext()).enqueue(workRequest2);

        return view;
    }

    private  void readFine(){






    }



    @Override
    public void refresh() {
        Log.d("NewsFragment", "Refreshing data...");

    }



    public boolean getLocalFile(ArrayList<ForexNewsItem> newsItemsList){
        Boolean isBol=null;
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_NEWS_ORGANISED);
        // Parse JSON data
        Type listType2 = new TypeToken<List<NewsOrganised>>() {}.getType();
        ArrayList<NewsOrganised> newsItems=new Gson().fromJson(readJsonData1, listType2);

        String readJsonData2 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME);
        // Parse JSON data
        Type listType3 = new TypeToken<List<ForexNewsItem>>() {}.getType();
        ArrayList<ForexNewsItem> testNews=new Gson().fromJson(readJsonData2, listType3);






        if(newsItems==null ||  testNews==null || testNews.size()==0){
            isBol=false;
            refresh.setVisibility(View.VISIBLE);
        }else {
            refresh.setVisibility(View.GONE);

            try {
                Calendar calendar = Calendar.getInstance();
                int DOW = calendar.get(Calendar.DAY_OF_WEEK);

                if(DOW==1){
                    newsItemsList.addAll(newsItems.get(0).getSundayNews());
                    newsItemsList.addAll(newsItems.get(0).getMondayNews());
                }else  if(DOW==2){
                    newsItemsList.addAll(newsItems.get(0).getMondayNews());
                }else  if(DOW==3){
                    newsItemsList.addAll(newsItems.get(0).getTuesdayNews());
                }else  if(DOW==4){
                    newsItemsList.addAll(newsItems.get(0).getWednesdayNews());
                }else  if(DOW==5){
                    newsItemsList.addAll(newsItems.get(0).getThursdayNews());
                }else  if(DOW==6){
                    newsItemsList.addAll(newsItems.get(0).getFridayNews());
                }else  if(DOW==7){
                    newsItemsList.addAll(newsItems.get(0).getSaturdayNews());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            forexNewsAdapter.notifyDataSetChanged();
//            newsItemsList.addAll(newsItems);
//            readFine();
            isBol= true;

        }
        return isBol;
    }



    public boolean getLocalFile_main(){
        forexNewsItems.clear();

        Boolean isBol=null;
        // Load  items from JSON
        String readJsonData3 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME);
        // Parse JSON data
        Type listType3 = new TypeToken<List<ForexNewsItem>>() {}.getType();
        ArrayList<ForexNewsItem> testNews=new Gson().fromJson(readJsonData3, listType3);


        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_NEWS_ORGANISED);
        // Parse JSON data
        Type listType2 = new TypeToken<List<NewsOrganised>>() {}.getType();
        ArrayList<NewsOrganised> newsItems=new Gson().fromJson(readJsonData1, listType2);



        if(testNews==null || testNews.size()==0){
            isBol=false;
            forexNewsItems=new ArrayList<>();
            refresh.setVisibility(View.VISIBLE);
        }else {

            refresh.setVisibility(View.GONE);

            try {
                Calendar calendar = Calendar.getInstance();
                int DOW = calendar.get(Calendar.DAY_OF_WEEK);

                if(DOW==1){
                    forexNewsItems.addAll(newsItems.get(0).getSundayNews());
                    forexNewsItems.addAll(newsItems.get(0).getMondayNews());
                }else  if(DOW==2){
                    forexNewsItems.addAll(newsItems.get(0).getMondayNews());
                }else  if(DOW==3){
                    forexNewsItems.addAll(newsItems.get(0).getTuesdayNews());
                }else  if(DOW==4){
                    forexNewsItems.addAll(newsItems.get(0).getWednesdayNews());
                }else  if(DOW==5){
                    forexNewsItems.addAll(newsItems.get(0).getThursdayNews());
                }else  if(DOW==6){
                    forexNewsItems.addAll(newsItems.get(0).getFridayNews());
                }else  if(DOW==7){
                    forexNewsItems.addAll(newsItems.get(0).getSaturdayNews());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            isBol= true;
            forexNewsAdapter.notifyDataSetChanged();

        }

        Toasty.success(getContext(), "Successfully updated news list!", Toast.LENGTH_SHORT, true).show();


        return isBol;
    }


    public boolean getLocalFile_main_(){
        forexNewsItems.clear();

        Boolean isBol=null;
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME);
        // Parse JSON data
        Type listType2 = new TypeToken<List<ForexNewsItem>>() {}.getType();
        ArrayList<ForexNewsItem> newsItems=new Gson().fromJson(readJsonData1, listType2);



        if(newsItems==null){
            isBol=false;
            forexNewsItems=new ArrayList<>();
            refresh.setVisibility(View.VISIBLE);
        }else {
            refresh.setVisibility(View.GONE);
            forexNewsItems.addAll(newsItems);
            readFine();
            isBol= true;

        }

        Toasty.success(getContext(), "Upcoming News Alert!", Toast.LENGTH_SHORT, true).show();


        return isBol;
    }


}
