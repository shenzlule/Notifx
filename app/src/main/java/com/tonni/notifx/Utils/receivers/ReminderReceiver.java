package com.tonni.notifx.Utils.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.Utils.workers.FetchWorker;
import com.tonni.notifx.Utils.workers.NotifyWorker;
import com.tonni.notifx.Utils.workers.ReminderWorker;
import com.tonni.notifx.models.ApiTurn;
import com.tonni.notifx.models.NotifWatchlistModel;
import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String FILE_NAME_NOTIFICATION = "notification.json";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("REMINDER", "class received");
        // Read JSON data from internal storage
        String readJsonData_notification_id = StorageUtils.readJsonFromFile(context, FILE_NAME_NOTIFICATION);

        // Parse JSON data

        Type listType_noti = new TypeToken<List<NotifWatchlistModel>>() {
        }.getType();
        ArrayList<NotifWatchlistModel> notifcation_list = new Gson().fromJson(readJsonData_notification_id, listType_noti);


        if ("REMINDER".equals(intent.getAction().toString())){
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class).build();
            WorkManager.getInstance(context).enqueue(workRequest);

        }else {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(context,  ReminderReceiver.class);
            // Create an intent
            intent2.setAction("REMINDER");

            Log.d("REMINDER", "REMINDER SET canceled  ");

            intent2.putExtra("delete", "yes");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 67676, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            Log.d("REMINDER", "REMINDER canceled ");
        }

        if ("REMINDER_delete".equals(intent.getAction().toString())){

            if (notifcation_list==null){

                // Remove the notification
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(67676);
                Log.d("REMINDER", "REMINDER SET canceled - null ");

            }else {
                if (notifcation_list.get(0).getNotification_Reminder() == 1) {
                    notifcation_list.get(0).setNotification_Reminder(0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent2 = new Intent(context, NotifyReceiver.class);
                    // Create an intent
                    intent2.setAction("REMINDER");

                    Log.d("REMINDER", "REMINDER SET canceled -not null ");

                    intent2.putExtra("delete", "yes");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 67676, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    Log.d("REMINDER", "REMINDER canceled  not null");

                    Gson gson = new Gson();
                    String jsonData_ = gson.toJson(notifcation_list);
                    StorageUtils.writeJsonToFile(context, FILE_NAME_NOTIFICATION, jsonData_);

                    // Remove the notification
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(67676);
                }


            }
        }



    }



}


