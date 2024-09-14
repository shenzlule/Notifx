package com.tonni.notifx.Utils.workers;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.Utils.receivers.AlertReceiver;
import com.tonni.notifx.models.ForexNewsItem;
import com.tonni.notifx.models.NewsOrganised;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class setNewsAlarmOnLoadWorker extends Worker {

    private static final String FILE_NAME_NEWS_ORGANISED = "forex_events_.json";

    Context context;
    public setNewsAlarmOnLoadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context=context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("Set_News_On_Open", "Set_News_On_Open<======>COMPLETED");
        setNews();
        return Result.success();
    }


    private  void setNews(){
        // Reset your alarms here
        List<ForexNewsItem> myBootlist = readFine(context);

        Calendar currentCalendar = Calendar.getInstance();
        if (myBootlist != null && myBootlist.size() > 0) {
            for (ForexNewsItem item : myBootlist) {
                Calendar itemCalendar = null;
                try {
                    itemCalendar = item.getCalendar();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        if (itemCalendar != null && itemCalendar.after(currentCalendar)) {
                            setAlarmForNewsItem(item, context);
                        }else {
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    private List<ForexNewsItem> readFine(Context context) {

        Calendar calendar = Calendar.getInstance();

        // Load Forex news items from JSON
        String readJsonData = StorageUtils.readJsonFromFile(context, FILE_NAME_NEWS_ORGANISED);
        ArrayList<ForexNewsItem> forexNewsItems=new ArrayList<>();


        // Parse JSON data
        Type listType = new TypeToken<List<NewsOrganised>>() {}.getType();
        List<NewsOrganised> orgNewsItems = new Gson().fromJson(readJsonData, listType);


        if (orgNewsItems == null) {
            forexNewsItems = new ArrayList<>();
        }else {
            try {
                int DOW = calendar.get(Calendar.DAY_OF_WEEK);

                if(DOW==1){
                    if (orgNewsItems.get(0).getSundayNews()!=null){
                        forexNewsItems.addAll(orgNewsItems.get(0).getSundayNews());
                        forexNewsItems.addAll(orgNewsItems.get(0).getMondayNews());
                    }
                }else  if(DOW==2){
                    if (orgNewsItems.get(0).getSundayNews()!=null){
                        forexNewsItems.addAll(orgNewsItems.get(0).getMondayNews());
                    }
                }else  if(DOW==3){
                    if (orgNewsItems.get(0).getSundayNews()!=null){
                        forexNewsItems.addAll(orgNewsItems.get(0).getTuesdayNews());
                    }
                }else  if(DOW==4){
                    if (orgNewsItems.get(0).getSundayNews()!=null){
                        forexNewsItems.addAll(orgNewsItems.get(0).getWednesdayNews());
                    }
                }else  if(DOW==5){
                    if (orgNewsItems.get(0).getSundayNews()!=null){
                        forexNewsItems.addAll(orgNewsItems.get(0).getThursdayNews());
                    }
                }else  if(DOW==6){
                    if (orgNewsItems.get(0).getSundayNews()!=null){
                        forexNewsItems.addAll(orgNewsItems.get(0).getFridayNews());
                    }
                }else  if(DOW==7){
                    if (orgNewsItems.get(0).getSundayNews()!=null){
                        forexNewsItems.addAll(orgNewsItems.get(0).getSaturdayNews());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }






        return forexNewsItems;
    }


    private void setAlarmForNewsItem(ForexNewsItem item, Context context) throws ParseException {
        Calendar calendar = item.getCalendar();
        Calendar nowCalendar=Calendar.getInstance();


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            long longItemTime=item.getCalendar().getTimeInMillis();
            long twoHour=longItemTime-(120*60*1000);
            long oneHour=longItemTime-(60*60*1000);
            long halfHour=longItemTime-(30*60*1000);
            long min_15_=longItemTime-(15*60*1000);
            long min_5_=longItemTime-(5*60*1000);
            long min_2_=longItemTime-(2*60*1000);

            long twoHour_=(120*60*1000);
            long oneHour_=(60*60*1000);
            long halfHour_=(30*60*1000);
            long min_15__=(15*60*1000);
            long min_5__=(5*60*1000);
            long min_2__=(2*60*1000);

            Calendar min_2_Calendar=Calendar.getInstance();
            Calendar min_5_Calendar=Calendar.getInstance();
            Calendar min_15_Calendar=Calendar.getInstance();
            Calendar halfHourCalendar=Calendar.getInstance();
            Calendar oneHourCalendar=Calendar.getInstance();
            Calendar twoHourCalendar=Calendar.getInstance();

            twoHourCalendar.setTimeInMillis(twoHour);
            oneHourCalendar.setTimeInMillis(oneHour);
            halfHourCalendar.setTimeInMillis(halfHour);
            min_15_Calendar.setTimeInMillis(min_15_);
            min_5_Calendar.setTimeInMillis(min_5_);
            min_2_Calendar.setTimeInMillis(min_2_);

            if(item.getCalendar().getTime().after(nowCalendar.getTime())){

                int id=(int) item.getId();
                Intent intent_main = new Intent(context, AlertReceiver.class);
                intent_main.putExtra("name", item.getName()+"[Now]");
                intent_main.putExtra("time", item.getTime());
                intent_main.putExtra("id", id);
                intent_main.setAction("News Alert Gringo");

                PendingIntent pendingIntent_now = PendingIntent.getBroadcast(context, id, intent_main, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, item.getCalendar().getTimeInMillis(), pendingIntent_now);
                }
                Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getCalendar().getTimeInMillis());
                Log.d("Boot Notifx", "id "+id);
                Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[Now]" + " " + item.getCalendar().getTimeInMillis());

                if(item.getCalendar().getTime().after(twoHourCalendar.getTime())  &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=twoHour_){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 2hrs]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+1);
                    intent.setAction("News Alert Gringo");

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, twoHourCalendar.getTimeInMillis(), pendingIntent);
                    }
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + twoHourCalendar.getTimeInMillis());
                    Log.d("Boot Notifx", "id "+id+1);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[Two hour]" + " " + twoHourCalendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(oneHourCalendar.getTime())  &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=oneHour_){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 1hr]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+2);
                    intent.setAction("News Alert Gringo");

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+2, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, oneHourCalendar.getTimeInMillis(), pendingIntent);
                    }
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + oneHourCalendar.getTimeInMillis());
                    Log.d("Boot Notifx", "id "+id+2);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[One hour]" + " " + oneHourCalendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(halfHourCalendar.getTime())  &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=halfHour_){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 30min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+3);
                    intent.setAction("News Alert Gringo");

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+3, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, halfHourCalendar.getTimeInMillis(), pendingIntent);
                    }
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + halfHourCalendar.getTimeInMillis());
                    Log.d("Boot Notifx", "id "+id+3);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[30min]" + " " + halfHourCalendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(min_15_Calendar.getTime()) &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=min_15__){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 15min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+4);
                    intent.setAction("News Alert Gringo");

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+4, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, min_15_Calendar.getTimeInMillis(), pendingIntent);
                    }
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + min_15_Calendar.getTimeInMillis());
                    Log.d("Boot Notifx", "id "+id+4);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[15 min]" + " " + min_15_Calendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(min_5_Calendar.getTime())  &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=min_5__){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 5min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+5);
                    intent.setAction("News Alert Gringo");

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+5, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, min_5_Calendar.getTimeInMillis(), pendingIntent);
                    }
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + min_5_Calendar.getTimeInMillis());
                    Log.d("Boot Notifx", "id "+id+5);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[5 min]" + " " + min_5_Calendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(min_2_Calendar.getTime()) &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=min_2__){
                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 2min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+6);
                    intent.setAction("News Alert Gringo");

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+6, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, min_2_Calendar.getTimeInMillis(), pendingIntent);
                    }
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + min_2_Calendar.getTimeInMillis());
                    Log.d("Boot Notifx", "id "+id+6);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[2 min]" + " " + min_2_Calendar.getTimeInMillis());

                }


            }

        } else {
            Log.d("Boot Notifx", "AlarmManager is null");
        }
    }


}
