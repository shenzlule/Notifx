package com.tonni.notifx.Utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.tonni.notifx.AlarmDetailsActivity;
import com.tonni.notifx.MainActivity;
import com.tonni.notifx.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String WAKE_LOCK_TAG = "myapp:alarmWakeLock";
    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final String FILE_NAME_PENDING = "pending.json";
    @Override
    public void onReceive(Context context, Intent intent) {

        // Acquire a wake lock to keep the device awake while handling the alarm
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
        wakeLock.acquire(60 * 1000L /*60 seconds*/);

        String name = intent.getStringExtra("name");
        String time = intent.getStringExtra("time");
        Log.d("AlarmReceiver", "Alarm received for " + name + " at " + time);

        // Create notification channel for Android O and above
        createNotificationChannel(context);

        // Create an intent to launch the AlarmDetailsActivity when the notification is tapped
        Intent notificationIntent = new Intent(context, AlarmDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        pendingIntent.putExtra("alarmDetails", String.valueOf(name +" "+time));

        // Create an intent for the stop button
        Intent stopIntent = new Intent(context, NotificationActionReceiver.class);
        stopIntent.setAction("STOP_VIBRATION");
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        // Load Forex news items from JSON
        String readJsonData = StorageUtils.readJsonFromFile(context, FILE_NAME_PENDING);
//        Log.d("PendingFragment", "Read JSON: " + readJsonData);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .setContentTitle(name)
                .setContentText(time)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(readJsonData)) // For longer text
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false) // Notification cannot be dismissed by swiping
                .setOngoing(true) // Make the notification ongoing
                .addAction(R.drawable.ic_baseline_not_interested_24, "Stop", stopPendingIntent) // Add stop button to notification
                .setVibrate(new long[]{0, 1000, 1000, 1000, 1000}); // Initial vibration pattern

        // Get the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        try {
            Log.d("AlarmReceiver", "Alarm received for " + name + " at " + time);
            // Create an intent to launch AlarmDetailsActivity

            // Here, you can add code to show a notification or perform other actions.
            Intent alarmIntent = new Intent(context, AlarmDetailsActivity.class);
            alarmIntent.putExtra("alarmDetails", String.valueOf(name +" "+time));
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // This is necessary to start an activity from a BroadcastReceiver

            // For Android 10 and above, start the activity with additional flags
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Log.d("AlarmReceiver-2", "Alarm received for " + name + " at " + time);
            }


            context.startActivity(alarmIntent);
        } finally {
            // Release the wake lock
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm Channel";
            String description = "Channel for alarm notifications";
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

