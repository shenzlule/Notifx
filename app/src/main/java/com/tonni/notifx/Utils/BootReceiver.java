package com.tonni.notifx.Utils;

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
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reset your alarms here
            List<ForexNewsItem> myBootlist = readFine(context);

            Calendar currentCalendar = Calendar.getInstance();
            if (myBootlist != null && myBootlist.size() > 0) {
                for (ForexNewsItem item : myBootlist) {
                    Calendar itemCalendar = null;
                    try {
                        itemCalendar = item.getCalendar();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            if (itemCalendar != null && itemCalendar.after(currentCalendar)) {
                                setAlarmForNewsItem(item, context);
                            }else {
//                    Toast.makeText(getContext(), "Not after"+String.valueOf(itemCalendar), Toast.LENGTH_SHORT).show();
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarmForNewsItem(ForexNewsItem item, Context context) throws ParseException {
        Calendar calendar = item.getCalendar();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("name", item.getName());
        intent.putExtra("time", item.getTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + calendar.getTimeInMillis());
            Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime() + " " + calendar.getTimeInMillis());
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
