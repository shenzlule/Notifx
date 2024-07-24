package com.tonni.notifx.Utils.receivers;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.tonni.notifx.AlarmDetailsActivity;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.Storage.StorageUtils;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String WAKE_LOCK_TAG = "myapp:alarmWakeLock";
    private static final String CHANNEL_ID = "alert_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final String FILE_NAME_PENDING = "pending.json";
    @Override
    public void onReceive(Context context, Intent intent) {

        // Acquire a wake lock to keep the device awake while handling the alarm
        String name = intent.getStringExtra("name");
        String time = intent.getStringExtra("time");
        Log.d("News Alert!!", "Alert received for " + name + " at " + time);

        // Create notification channel for Android O and above
        createNotificationChannel(context);



        // Create an intent for the stop button
        Intent stopIntent = new Intent(context, NotificationActionReceiver.class);
        stopIntent.setAction("STOP_VIBRATION_NEWS");
        int id_=(int) System.currentTimeMillis();
        stopIntent.putExtra("id",id_);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);



        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notif)
                .setContentTitle(name)
                .setContentText(time)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(name)) // For longer text
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false) // Notification cannot be dismissed by swiping
                .setOngoing(true) // Make the notification ongoing
                .addAction(R.drawable.ic_baseline_delete_forever_24, "Remove", stopPendingIntent) // Add stop button to notification
                .setVibrate(new long[]{0, 1000, 1000, 1000, 1000}); // Initial vibration pattern

        // Get the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id_, builder.build());


    }


    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alert Channel";
            String description = "Channel for alert notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }






    }


//
//

