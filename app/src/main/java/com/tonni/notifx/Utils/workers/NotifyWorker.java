package com.tonni.notifx.Utils.workers;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.tonni.notifx.R;

public class NotifyWorker extends Worker {
    private static final int MAX_PRIORITY = 9;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Create notification
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "notify_20_min";
        String channelName = "20 Minute Notification";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle("Watch list Check")
                .setContentText("Fetching data...")
                .setPriority(MAX_PRIORITY)
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .build();

        notificationManager.notify(1, notification);


        return Result.success();
    }




}
