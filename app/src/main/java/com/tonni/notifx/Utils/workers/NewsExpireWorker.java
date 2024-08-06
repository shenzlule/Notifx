package com.tonni.notifx.Utils.workers;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.models.ForexNewsItem;
import com.tonni.notifx.models.NewsOrganised;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewsExpireWorker extends Worker {


    private Context context;

    private static final String TAG = "ICMP";
    private static final int REQUEST_SET_ALARM = 1;
    ArrayList<ForexNewsItem> validItems;
    private static final String FILE_NAME_NEWS_ORGANISED = "forex_events_.json";

    private static final String FILE_NAME = "forex_events.json";
    private ArrayList<ForexNewsItem> forexNewsItems;


    public NewsExpireWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
        this.forexNewsItems =new ArrayList<>() ;
    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            if(getLocalFile(this.forexNewsItems)){

                saved_null_files();
//                send broadcast
                Intent intent= new Intent("android.intent.action.WithInMain");
                context.sendBroadcast(intent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Result.success();
    }



    public boolean getLocalFile(ArrayList<ForexNewsItem> newsItemsList) throws ParseException {
        Boolean sendBroadcast=null;
        sendBroadcast=false;

        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME_NEWS_ORGANISED);
        // Parse JSON data
        Type listType2 = new TypeToken<List<NewsOrganised>>() {}.getType();
        ArrayList<NewsOrganised> newsItems=new Gson().fromJson(readJsonData1, listType2);

        String readJsonData2 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME);
        // Parse JSON data
        Type listType3 = new TypeToken<List<ForexNewsItem>>() {}.getType();
        ArrayList<ForexNewsItem> testNews=new Gson().fromJson(readJsonData2, listType3);


        for (int i = 0; i < testNews.size(); i++) {

            if(testNews.get(i).getCalendar().after(Calendar.getInstance())){
                sendBroadcast=false;
                break;
            }
            else {
                sendBroadcast=true;
            }

        }

        return sendBroadcast;
    }


    private  Context getContext(){
        return this.context;
    }


    public  void saved_null_files(){
        Gson gson = new Gson();
        //CLEAR
        ArrayList<ForexNewsItem> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_NEWS_ORGANISED, jsonData_);
        StorageUtils.writeJsonToFile(getContext(), FILE_NAME, jsonData_);


    }
}
