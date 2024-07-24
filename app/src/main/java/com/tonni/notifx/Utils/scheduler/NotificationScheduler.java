package com.tonni.notifx.Utils.scheduler;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tonni.notifx.Utils.receivers.NotifyReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class NotificationScheduler {
    public static void scheduleNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotifyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int currentMinute = calendar.get(Calendar.MINUTE);
        if (currentMinute < 10) {
            calendar.set(Calendar.MINUTE, 10);
        } else if(currentMinute > 10 && currentMinute < 20 ){
            calendar.set(Calendar.MINUTE, 20);

        } else if(currentMinute > 20 && currentMinute < 30 ){
            calendar.set(Calendar.MINUTE, 30);

        } else if(currentMinute > 30 && currentMinute < 40 ){
            calendar.set(Calendar.MINUTE, 40);

        } else if(currentMinute > 40 && currentMinute < 50 ){
            calendar.set(Calendar.MINUTE, 50);

        } else {
            calendar.set(Calendar.MINUTE, 0);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.MINUTE, 10);
        }
        // Set the repeating interval to 20 minutes (in milliseconds)
        long intervalMillis = 10 * 60 * 1000;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, pendingIntent);
        // Format the calendar time to a readable date and time string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        // Log the formatted date and time
        Log.d("MainActivity-api", "Alarm set for " + "Api" + " at " + formattedDate + " (" + calendar.getTimeInMillis() + ")");
    }
}
