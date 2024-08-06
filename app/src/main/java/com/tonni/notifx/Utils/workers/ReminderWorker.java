package com.tonni.notifx.Utils.workers;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.Utils.receivers.NotifyReceiver;
import com.tonni.notifx.Utils.receivers.ReminderReceiver;
import com.tonni.notifx.models.NotifWatchlistModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReminderWorker extends Worker {
    private static final int MAX_PRIORITY = 9;
    private static final String FILE_NAME_NOTIFICATION = "notification.json";

    Context context;
    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
    }

    @NonNull
    @Override
    public Result doWork() {



        Intent intent = new Intent(context, ReminderReceiver.class);
        // Create an intent
        intent.setAction("REMINDER_delete");

        Log.d("REMINDER_delete", "REMINDER SET");

        intent.putExtra("delete","yes");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 676766, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        // Create notification
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "notify_10_min";
        String channelName = "1min Reminder Notification";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle("Watch list Reminder")
                .setContentText("Check filled watch list.")
                .setPriority(MAX_PRIORITY)
                .setAutoCancel(false)
                .addAction(R.drawable.ic_baseline_delete_forever_24, "Remove reminder", pendingIntent)
                .setCategory(Notification.CATEGORY_ALARM)
                .setSmallIcon(R.drawable.notif)
                .setOngoing(true) // Make the notification ongoing
                .setVibrate(new long[]{1000, 1000, 1000, 1000,1000}) // Initial vibration pattern
                .build();

        notificationManager.notify(67676, notification);


        return Result.success();
    }




}
