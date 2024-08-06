package com.tonni.notifx.Utils.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.os.Vibrator;
import android.util.Log;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.models.ApiTurn;
import com.tonni.notifx.models.NotifWatchlistModel;
import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationActionReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1;


    @Override
    public void onReceive(Context context, Intent intent) {
        if ("STOP_VIBRATION_NEWS".equals(intent.getAction())) {
            // Stop the vibration
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.cancel();
            }
            Log.d("STOP_VIBRATION_NEWS",  intent.getExtras().get("id").toString());

            // Remove the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel((Integer) intent.getExtras().get("id"));

        }

        if ("WATCH_LIST_ALERT".equals(intent.getAction())) {
            Log.d("WATCH_LIST_ALERT",  intent.getExtras().get("id").toString());

            // Remove the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel((Integer)intent.getExtras().get("id"));

        }

        if ("News Alert Gringo".equals(intent.getAction())) {
            Log.d("News Alert Gringo",  "Canceled"+"  "+intent.getExtras().get("id").toString());

            // Remove the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel((Integer)intent.getExtras().get("id"));

        }

    }
}
