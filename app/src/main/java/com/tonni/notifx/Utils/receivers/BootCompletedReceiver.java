package com.tonni.notifx.Utils.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tonni.notifx.MainActivity;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.Utils.scheduler.NotificationScheduler;
import com.tonni.notifx.models.ForexNewsItem;
import com.tonni.notifx.models.NewsOrganised;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final  String CHANNEL_ID = "Api 10 interval data";
    private static final  String channelName = "10 Minute Api Notification";
    private static final int NOTIFICATION_ID = 575757;
    private static final String FILE_NAME_NEWS_ORGANISED = "forex_events_.json";

    private  Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootCompletedReceiver", "LOCK BOOT COMPLETED BOOT COMPLETED BOOT COMPLETED BOOT COMPLETED BOOT COMPLETED");
            sendNotification(context);
            setNews();
            setDailyAlarm();
        }
    }

    private void sendNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String description = "Channel for Notifx notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationScheduler.scheduleNotification(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notif)
                .setContentTitle("Notifx-Boot")
                .setContentText("Monitor watch list.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
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

                if(item.getCalendar().getTime().after(twoHourCalendar.getTime())){

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
                if(item.getCalendar().getTime().after(oneHourCalendar.getTime())){

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
                if(item.getCalendar().getTime().after(halfHourCalendar.getTime())){

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
                if(item.getCalendar().getTime().after(min_15_Calendar.getTime())){

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
                if(item.getCalendar().getTime().after(min_5_Calendar.getTime())){

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
                if(item.getCalendar().getTime().after(min_2_Calendar.getTime())){
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


    private void setDailyAlarm() {
        // Get AlarmManager instance
        AlarmManager alarmManager = (AlarmManager)  context.getSystemService(Context.ALARM_SERVICE);

        // Create an Intent to broadcast when the alarm goes off
        Intent intent = new Intent(context, NewDayReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the time for the alarm (00:01 every day)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND, 0);

        // Ensure the alarm triggers at the correct time if it’s already past today’s time
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Set a repeating alarm that triggers every 24 hours
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,  // Wake the device to trigger the alarm
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, // Repeat every day
                pendingIntent
        );
    }
}
