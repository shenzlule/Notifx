package com.tonni.notifx.Utils.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.models.ForexNewsItem;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    private static final String FILE_NAME = "forex_events.json";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
            oneHourCalendar.setTimeInMillis(twoHour);
            halfHourCalendar.setTimeInMillis(twoHour);
            min_15_Calendar.setTimeInMillis(twoHour);
            min_5_Calendar.setTimeInMillis(twoHour);
            min_2_Calendar.setTimeInMillis(twoHour);

            if(item.getCalendar().getTime().after(nowCalendar.getTime())){

                int id=(int) item.getId();
                Intent intent_main = new Intent(context, AlertReceiver.class);
                intent_main.putExtra("name", item.getName()+"[Now]");
                intent_main.putExtra("time", item.getTime());
                intent_main.putExtra("id", id);

                PendingIntent pendingIntent_now = PendingIntent.getBroadcast(context, id, intent_main, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, item.getCalendar().getTimeInMillis(), pendingIntent_now);
                Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getCalendar().getTimeInMillis());
                Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[Now]" + " " + item.getCalendar().getTimeInMillis());

                if(item.getCalendar().getTime().after(twoHourCalendar.getTime())){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 2hrs]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+1);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, twoHourCalendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + twoHourCalendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[Two hour]" + " " + twoHourCalendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(oneHourCalendar.getTime())){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 1hr]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+2);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+2, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, oneHourCalendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + oneHourCalendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[One hour]" + " " + oneHourCalendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(halfHourCalendar.getTime())){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 30min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+3);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+3, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, halfHourCalendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + halfHourCalendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[30min]" + " " + halfHourCalendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(min_15_Calendar.getTime())){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 15min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+4);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+4, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, min_15_Calendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + min_15_Calendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[15 min]" + " " + min_15_Calendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(min_5_Calendar.getTime())){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 5min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+5);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+5, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, min_5_Calendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + min_5_Calendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[15 min]" + " " + min_5_Calendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(min_2_Calendar.getTime())){
                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 2min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+6);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+6, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, min_2_Calendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + min_2_Calendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[15 min]" + " " + min_2_Calendar.getTimeInMillis());

                }


            }

        } else {
            Log.d("Boot Notifx", "AlarmManager is null");
        }
    }

    private List<ForexNewsItem> readFine(Context context) {
        // Load Forex news items from JSON
        String readJsonData = StorageUtils.readJsonFromFile(context, FILE_NAME);

        // Parse JSON data
        Type listType = new TypeToken<List<ForexNewsItem>>() {}.getType();
        List<ForexNewsItem> forexNewsItems = new Gson().fromJson(readJsonData, listType);




        if (forexNewsItems == null) {
            forexNewsItems = new ArrayList<>();
        }

        return forexNewsItems;
    }
}
