package com.tonni.notifx.Utils.scheduler;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tonni.notifx.Utils.receivers.NotifyReceiver;
import com.tonni.notifx.Utils.receivers.ReminderReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ReminderScheduler {
    public static void scheduleReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        // Create an intent
        intent.setAction("REMINDER");

        Log.d("REMINDER", "REMINDER SET");

        intent.putExtra("delete","yes");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 67676, intent, PendingIntent.FLAG_UPDATE_CURRENT );



        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE,1);


        // Set the repeating interval to 2 minute (in milliseconds)
        long intervalMillis = 2 * 60 * 1000;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, pendingIntent);
        // Format the calendar time to a readable date and time string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
//        alarmManager.cancel(pendingIntent);

        // Log the formatted date and time
        Log.d("REMINDER", "REMINDER set for " + "reminder" + " at " + formattedDate + " (" + calendar.getTimeInMillis() + ")");
    }
}
