package com.tonni.notifx.Utils.receivers;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.models.ApiTurn;
import com.tonni.notifx.models.NotifAlertModel;
import com.tonni.notifx.models.NotifWatchlistModel;
import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlertReceiver extends BroadcastReceiver {

    private static final String WAKE_LOCK_TAG = "myapp:alarmWakeLock";
    private static final String CHANNEL_ID = "alert_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final String FILE_NAME_PENDING = "pending.json";
    private static final String FILE_NAME_NOTIFICATION = "notification_alert.json";
    @Override
    public void onReceive(Context context, Intent intent) {

        if("News Alert Gringo".equals(intent.getAction())){

            String name = intent.getStringExtra("name");
            String time = intent.getStringExtra("time");
            int id_num = intent.getIntExtra("id",40000);
            Log.d("News Alert!!", "Alert received for " + name + " at " + time +"  id="+id_num);

            // Create notification channel for Android O and above
            createNotificationChannel(context);


            // Create an intent for the stop button
            Intent stopIntent = new Intent(context, NotificationActionReceiver.class);
            stopIntent.setAction("News Alert Gringo");
            stopIntent.putExtra("id",id_num);
            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, id_num, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);



            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notif)
                    .setContentTitle(name)
                    .setContentText(time)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(name)) // For longer text
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true) // Notification  be dismissed by swiping
                    .setOngoing(false) // Make the notification ongoing
//                    .addAction(R.drawable.ic_baseline_delete_forever_24, "Remove", stopPendingIntent) // Add stop button to notification
                    .setVibrate(new long[]{1000, 1000, 1000, 1000}); // Initial vibration pattern

            // Get the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id_num, builder.build());


//            Intent intent_alertUi_newsFrag= new Intent("android.intent.action.WithInMainNewsFrag");
//            context.sendBroadcast(intent_alertUi_newsFrag);



        }else {
            String name = intent.getStringExtra("name");
            String time = intent.getStringExtra("time");
//            Log.d("News Alert!!", "Alert received for " + name + " at " + time);

            // Create notification channel for Android O and above
            createNotificationChannel(context);


            String readJsonData_notification_id = StorageUtils.readJsonFromFile(context, FILE_NAME_NOTIFICATION);
            // Parse JSON data

            Type listType_noti = new TypeToken<List<NotifAlertModel>>() {
            }.getType();
            ArrayList<NotifAlertModel> notifcation_list = new Gson().fromJson(readJsonData_notification_id, listType_noti);

            if (notifcation_list==null || notifcation_list.size()==0){
                notifcation_list=new ArrayList<>();
                notifcation_list.add(0,new NotifAlertModel(2000));
            }
            int count_notification = notifcation_list.get(0).getNotification_id_alert();


            count_notification++;

            // Create an intent for the stop button
            Intent stopIntent = new Intent(context, NotificationActionReceiver.class);
            stopIntent.setAction("STOP_VIBRATION_NEWS");
            int id_=count_notification;
            stopIntent.putExtra("id",id_);
            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, id_, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);



            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notif)
                    .setContentTitle(name)
                    .setContentText(time)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(name)) // For longer text
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(false) // Notification  be dismissed by swiping
                    .setOngoing(true) // Make the notification ongoing
                    .addAction(R.drawable.ic_baseline_delete_forever_24, "Remove", stopPendingIntent) // Add stop button to notification
                    .setVibrate(new long[]{1000, 1000, 1000, 1000}); // Initial vibration pattern

            // Get the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(id_, builder.build());



            Gson gson=new Gson();
            String jsonData_notification_list = gson.toJson(notifcation_list);
            StorageUtils.writeJsonToFile(context, FILE_NAME_NOTIFICATION, jsonData_notification_list);
        }



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

