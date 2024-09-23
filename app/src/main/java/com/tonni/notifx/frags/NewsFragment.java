package com.tonni.notifx.frags;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    private ArrayList<NewsOrganised> forexNewsItems_for_active_btns;
    private ForexNewsAdapter forexNewsAdapter;
    private RecyclerView recyclerView;
    private String readJsonData;
    private Button refresh_btn;
    private RelativeLayout All_btn, Mon_btn, Tue_btn, Wed_btn, Thur_btn, Fri_btn, Weekend_btn;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);


        All_btn =view.findViewById(R.id.textViewAll);
        Mon_btn =view.findViewById(R.id.textViewMon);
        Tue_btn =view.findViewById(R.id.textViewTue);
        Wed_btn =view.findViewById(R.id.textViewWed);
        Thur_btn =view.findViewById(R.id.textViewThur);
        Fri_btn =view.findViewById(R.id.textViewFri);
        Weekend_btn =view.findViewById(R.id.textViewWeekend);

        Weekend_btn.setBackgroundResource(0);
        All_btn.setBackgroundResource(0);
        Mon_btn.setBackgroundResource(0);
        Tue_btn.setBackgroundResource(0);
        Wed_btn.setBackgroundResource(0);
        Thur_btn.setBackgroundResource(0);
        Fri_btn.setBackgroundResource(0);



        refresh_btn =view.findViewById(R.id.refresh);
        refresh_btn.setVisibility(View.GONE);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
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
        forexNewsItems_for_active_btns=new ArrayList<>();


        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewForexNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Set adapter
        forexNewsAdapter = new ForexNewsAdapter(forexNewsItems,getContext());
        recyclerView.setAdapter(forexNewsAdapter);


        int  dayActiva=getLocalFile(forexNewsItems).get(1);
        if (dayActiva==1){
            Weekend_btn.setBackgroundResource(R.drawable.border_active);
            All_btn.setBackgroundResource(0);
            Mon_btn.setBackgroundResource(0);
            Tue_btn.setBackgroundResource(0);
            Wed_btn.setBackgroundResource(0);
            Thur_btn.setBackgroundResource(0);
            Fri_btn.setBackgroundResource(0);
        }else if(dayActiva==2){
            Weekend_btn.setBackgroundResource(0);
            All_btn.setBackgroundResource(0);
            Mon_btn.setBackgroundResource(R.drawable.border_active);
            Tue_btn.setBackgroundResource(0);
            Wed_btn.setBackgroundResource(0);
            Thur_btn.setBackgroundResource(0);
            Fri_btn.setBackgroundResource(0);
        }else if(dayActiva==3){
            Weekend_btn.setBackgroundResource(0);
            All_btn.setBackgroundResource(0);
            Mon_btn.setBackgroundResource(0);
            Tue_btn.setBackgroundResource(R.drawable.border_active);
            Wed_btn.setBackgroundResource(0);
            Thur_btn.setBackgroundResource(0);
            Fri_btn.setBackgroundResource(0);
        }else if(dayActiva==4){
            Weekend_btn.setBackgroundResource(0);
            All_btn.setBackgroundResource(0);
            Mon_btn.setBackgroundResource(0);
            Tue_btn.setBackgroundResource(0);
            Wed_btn.setBackgroundResource(R.drawable.border_active);
            Thur_btn.setBackgroundResource(0);
            Fri_btn.setBackgroundResource(0);
        }else if(dayActiva==5){

            Weekend_btn.setBackgroundResource(0);
            All_btn.setBackgroundResource(0);
            Mon_btn.setBackgroundResource(0);
            Tue_btn.setBackgroundResource(0);
            Wed_btn.setBackgroundResource(0);
            Thur_btn.setBackgroundResource(R.drawable.border_active);
            Fri_btn.setBackgroundResource(0);
        }else if(dayActiva==6){
            Weekend_btn.setBackgroundResource(0);
            All_btn.setBackgroundResource(0);
            Mon_btn.setBackgroundResource(0);
            Tue_btn.setBackgroundResource(0);
            Wed_btn.setBackgroundResource(0);
            Thur_btn.setBackgroundResource(0);
            Fri_btn.setBackgroundResource(R.drawable.border_active);

        }else if(dayActiva==7){
            Weekend_btn.setBackgroundResource(R.drawable.border_active);
            All_btn.setBackgroundResource(0);
            Mon_btn.setBackgroundResource(0);
            Tue_btn.setBackgroundResource(0);
            Wed_btn.setBackgroundResource(0);
            Thur_btn.setBackgroundResource(0);
            Fri_btn.setBackgroundResource(0);
        }


        OneTimeWorkRequest workRequest2 = new OneTimeWorkRequest.Builder(NewsExpireWorker.class).build();
        WorkManager.getInstance(getContext()).enqueue(workRequest2);

        setActiveButtons();

        return view;
    }



    private  void readFine(){

    }



    @Override
    public void refresh() {
        Log.d("NewsFragment", "Refreshing data...");

    }



    public ArrayList<Integer> getLocalFile(ArrayList<ForexNewsItem> newsItemsList){
        Boolean isBol=null;
        int dayActive= 100;
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_NEWS_ORGANISED);
        // Parse JSON data
        Type listType2 = new TypeToken<List<NewsOrganised>>() {}.getType();
        ArrayList<NewsOrganised> newsItems=new Gson().fromJson(readJsonData1, listType2);

        String readJsonData2 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME);
        // Parse JSON data
        Type listType3 = new TypeToken<List<ForexNewsItem>>() {}.getType();
        ArrayList<ForexNewsItem> testNews=new Gson().fromJson(readJsonData2, listType3);

        // copy items
        forexNewsItems_for_active_btns.addAll(newsItems);






        if(newsItems==null ||  testNews==null || testNews.size()==0){
            isBol=false;
            refresh_btn.setVisibility(View.VISIBLE);
        }else {
            refresh_btn.setVisibility(View.GONE);

            try {
                Calendar calendar = Calendar.getInstance();
                int DOW = calendar.get(Calendar.DAY_OF_WEEK);

                if(DOW==1){
                    newsItemsList.addAll(newsItems.get(0).getSundayNews());
                    newsItemsList.addAll(newsItems.get(0).getMondayNews());
                    dayActive=1;
                }else  if(DOW==2){
                    newsItemsList.addAll(newsItems.get(0).getMondayNews());
                    dayActive=2;
                }else  if(DOW==3){
                    newsItemsList.addAll(newsItems.get(0).getTuesdayNews());
                    dayActive=3;
                }else  if(DOW==4){
                    newsItemsList.addAll(newsItems.get(0).getWednesdayNews());
                    dayActive=4;
                }else  if(DOW==5){
                    newsItemsList.addAll(newsItems.get(0).getThursdayNews());
                    dayActive=5;
                }else  if(DOW==6){
                    newsItemsList.addAll(newsItems.get(0).getFridayNews());
                    dayActive=6;
                }else  if(DOW==7){
                    newsItemsList.addAll(newsItems.get(0).getSaturdayNews());
                    dayActive=7;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            forexNewsAdapter.notifyDataSetChanged();
//            newsItemsList.addAll(newsItems);
//            readFine();
            isBol= true;

        }

        ArrayList<Integer> returnArray=new ArrayList<>();
        if (isBol){
            returnArray.add(0,1);
        }else {
            returnArray.add(0,0);
        }

        returnArray.add(1,dayActive);

        return returnArray;
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
            refresh_btn.setVisibility(View.VISIBLE);
        }else {

            refresh_btn.setVisibility(View.GONE);

            int  dayActive_=100;

            try {
                Calendar calendar = Calendar.getInstance();
                int DOW = calendar.get(Calendar.DAY_OF_WEEK);

                if(DOW==1){
                    forexNewsItems.addAll(newsItems.get(0).getSundayNews());
                    forexNewsItems.addAll(newsItems.get(0).getMondayNews());
                    dayActive_=1;
                }else  if(DOW==2){
                    forexNewsItems.addAll(newsItems.get(0).getMondayNews());
                    dayActive_=2;
                }else  if(DOW==3){
                    forexNewsItems.addAll(newsItems.get(0).getTuesdayNews());
                    dayActive_=3;
                }else  if(DOW==4){
                    forexNewsItems.addAll(newsItems.get(0).getWednesdayNews());
                    dayActive_=4;
                }else  if(DOW==5){
                    forexNewsItems.addAll(newsItems.get(0).getThursdayNews());
                    dayActive_=5;
                }else  if(DOW==6){
                    forexNewsItems.addAll(newsItems.get(0).getFridayNews());
                    dayActive_=6;
                }else  if(DOW==7){
                    forexNewsItems.addAll(newsItems.get(0).getSaturdayNews());
                    dayActive_=7;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            isBol= true;
            if (dayActive_==1){
                Weekend_btn.setBackgroundResource(R.drawable.border_active);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(0);
            }else if(dayActive_==2){
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(R.drawable.border_active);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(0);
            }else if(dayActive_==3){
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(R.drawable.border_active);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(0);
            }else if(dayActive_==4){
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(R.drawable.border_active);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(0);
            }else if(dayActive_==5){

                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(R.drawable.border_active);
                Fri_btn.setBackgroundResource(0);
            }else if(dayActive_==6){
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(R.drawable.border_active);

            }else if(dayActive_==7){
                Weekend_btn.setBackgroundResource(R.drawable.border_active);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(0);
            }

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
            refresh_btn.setVisibility(View.VISIBLE);
        }else {
            refresh_btn.setVisibility(View.GONE);
            forexNewsItems.addAll(newsItems);
            readFine();
            isBol= true;

        }

        Toasty.success(getContext(), "Upcoming News Alert!", Toast.LENGTH_SHORT, true).show();


        return isBol;
    }


    private void setActiveButtons(){
        All_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(R.drawable.border_active);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(0);

                forexNewsItems.clear();
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getMondayNews());
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getTuesdayNews());
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getWednesdayNews());
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getThursdayNews());
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getFridayNews());
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getSaturdayNews());
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getSundayNews());

                forexNewsAdapter.notifyDataSetChanged();

            }
        });

        Weekend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weekend_btn.setBackgroundResource(R.drawable.border_active);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(0);

                forexNewsItems.clear();

                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getSaturdayNews());
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getSundayNews());

                forexNewsAdapter.notifyDataSetChanged();
            }
        });

        Mon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(R.drawable.border_active);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);;
                Fri_btn.setBackgroundResource(0);

                forexNewsItems.clear();
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getMondayNews());


                forexNewsAdapter.notifyDataSetChanged();
            }
        });

        Tue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(R.drawable.border_active);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(0);

                forexNewsItems.clear();
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getTuesdayNews());


                forexNewsAdapter.notifyDataSetChanged();
            }
        });
        Wed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(R.drawable.border_active);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(0);

                forexNewsItems.clear();
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getWednesdayNews());


                forexNewsAdapter.notifyDataSetChanged();
            }
        });

        Thur_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(R.drawable.border_active);
                Fri_btn.setBackgroundResource(0);

                forexNewsItems.clear();
                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getThursdayNews());

                forexNewsAdapter.notifyDataSetChanged();
            }
        });

        Fri_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weekend_btn.setBackgroundResource(0);
                All_btn.setBackgroundResource(0);
                Mon_btn.setBackgroundResource(0);
                Tue_btn.setBackgroundResource(0);
                Wed_btn.setBackgroundResource(0);
                Thur_btn.setBackgroundResource(0);
                Fri_btn.setBackgroundResource(R.drawable.border_active);

                forexNewsItems.clear();

                forexNewsItems.addAll(forexNewsItems_for_active_btns.get(0).getFridayNews());

                forexNewsAdapter.notifyDataSetChanged();
            }
        });


    }

}
