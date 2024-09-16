package com.tonni.notifx.Utils.workers;


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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.Utils.receivers.NotificationActionReceiver;
import com.tonni.notifx.Utils.scheduler.ReminderScheduler;
import com.tonni.notifx.api.ApiResponse;
import com.tonni.notifx.models.ApiCount;
import com.tonni.notifx.models.ApiTurn;
import com.tonni.notifx.models.ForexCurrency;
import com.tonni.notifx.models.NotifWatchlistModel;
import com.tonni.notifx.models.PendingPrice;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FetchWorker extends Worker {

    private static final String URL = "https://marketdata.tradermade.com/api/v1/live";
    private static final String URL2 = "https://marketdata.tradermade.com/api/v1/live";
    private static final String CURRENCY = "USDJPY,USA30,GBPUSD,EURUSD,XAUUSD,USDCAD,USDCHF,EURJPY,GBPJPY,NZDCAD,CHFJPY,CADCHF,CADJPY";
    private static final String API_KEY = "HvHNPZP8zjXZuRYwAj-S";
    private static final String API_KEY2 = "BlQh6jBbdKB_F_31ZtKL";

    private static final String FILE_NAME_PENDING = "pending.json";
    private static final String FILE_NAME_TURN = "turnApi.json";
    private static final String FILE_NAME_FILLED_LOCAL = "filled.json";
    private static final String FILE_NAME_PENDING_FOREX_LOCAL = "pending_forex.json";
    private static final String FILE_NAME_PENDING_LOCAL = "pending_pending.json";
    private static final String FILE_NAME_NOTIFICATION = "notification.json";
    private static final String FILE_NAME_CURRENCIES_LOCAL = "currencies.json";
    private static final String FILE_NAME_API_COUNT = "api_count.json";


    private static final int MAX_PRIORITY = 10;
    private String queryUrl = URL + "?currency=" + CURRENCY + "&api_key=" + API_KEY;
    private String queryUrl_1 = URL + "?currency=" + CURRENCY + "&api_key=" + API_KEY;
    private String queryUrl_2 = URL + "?currency=" + CURRENCY + "&api_key=" + API_KEY2;

    private Context context;

    public FetchWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        fetchData();
        return Result.success();
    }


    private void fetchData() {

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);


        // Define the days of the week to check (Monday to Friday in this case)
        boolean isInWeekDays = dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY;

        // Define the time range to check (6:00 AM to 10:00 PM)
        boolean isInTimeRange = hourOfDay >= 6 && hourOfDay < 22;


        if (isInWeekDays && isInTimeRange) {
            // Call the method

            // Load Forex news items from JSON
            String readJsonData_valid = StorageUtils.readJsonFromFile(context, FILE_NAME_PENDING);

            // Parse JSON data
            Type listType = new TypeToken<List<PendingPrice>>() {
            }.getType();
            List<PendingPrice> testingValiditityList = new Gson().fromJson(readJsonData_valid, listType);
            ArrayList<String> testingValiditityList_string = new ArrayList<>();


            if (testingValiditityList == null) {
                testingValiditityList = new ArrayList<PendingPrice>();
            }
            int getWaitingList = 0;

            for (int i = 0; i < testingValiditityList.size(); i++) {
                if (testingValiditityList.get(i).getFilled().equals("Not")) {
                    getWaitingList++;
                    testingValiditityList_string.add(testingValiditityList.get(i).getPair());

                }
            }

            String new_currency_string=new_pairs_to_api(testingValiditityList_string);
            queryUrl = URL + "?currency=" + new_currency_string + "&api_key=" + API_KEY;
            queryUrl_1 = URL + "?currency=" + new_currency_string + "&api_key=" + API_KEY;
            queryUrl_2 = URL + "?currency=" + new_currency_string + "&api_key=" + API_KEY2;


            if (getWaitingList > 0) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);


        //get turn
        // Read JSON data from internal storage
        String readJsonData_turn = StorageUtils.readJsonFromFile(context, FILE_NAME_TURN);
        String readJsonData_Api_Count = StorageUtils.readJsonFromFile(context, FILE_NAME_API_COUNT);
        String readJsonData_filled = StorageUtils.readJsonFromFile(context, FILE_NAME_FILLED_LOCAL);
        String readJsonData_notification_id = StorageUtils.readJsonFromFile(context, FILE_NAME_NOTIFICATION);
        // Parse JSON data
        Type listType_turn = new TypeToken<List<ApiTurn>>() {
        }.getType();

        Type listType_api_count = new TypeToken<List<ApiCount>>() {
                }.getType();

        Type listType_filled = new TypeToken<List<PendingPrice>>() {
                }.getType();
        Type listType_noti = new TypeToken<List<NotifWatchlistModel>>() {
                }.getType();
        List<ApiTurn> turnList = new Gson().fromJson(readJsonData_turn, listType_turn);
        ArrayList<PendingPrice> filled_list = new Gson().fromJson(readJsonData_filled, listType_filled);
        ArrayList<NotifWatchlistModel> notifcation_list = new Gson().fromJson(readJsonData_notification_id, listType_noti);
        ArrayList<ApiCount> api_count_list = new Gson().fromJson(readJsonData_Api_Count, listType_api_count);

        if (filled_list==null){
            filled_list=new ArrayList<>();
        }

        if (api_count_list==null){
                    api_count_list=new ArrayList<>();
                    api_count_list.add(0,new ApiCount(0,0,0,0));
                }


        if (notifcation_list==null || notifcation_list.size()==0){
                    notifcation_list=new ArrayList<>();
                    notifcation_list.add(0,new NotifWatchlistModel(1000,0));
        }
                final int[] count_notification = {notifcation_list.get(0).getNotification_id_watch_list()};
                final int[] count_notification_ = {notifcation_list.get(0).getNotification_id_watch_list()};


                if (turnList==null){
            turnList=new ArrayList<>();

            turnList.add(0,new ApiTurn(1));
        }else if(turnList.get(0).getTurn_number()== 1){
            //make use of one api and flip for the next turn
            turnList.clear();
            turnList.add(0,new ApiTurn(2));
            queryUrl = queryUrl_1;
        }else if(turnList.get(0).getTurn_number() == 2){
            //make use of one api and flip for the next turn
            turnList.clear();
            turnList.add(0,new ApiTurn(1));
            queryUrl = queryUrl_2;
        }






                List<ApiTurn> finalTurnList = turnList;
                ArrayList<PendingPrice> finalFilled_list = filled_list;
                ArrayList<NotifWatchlistModel> finalNotifcation_list = notifcation_list;
                List<ApiTurn> finalTurnList1 = turnList;
                ArrayList<ApiCount> finalApi_count_list = api_count_list;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                queryUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the JSON response
                        Gson gson = new Gson();
                        ApiResponse apiResponse = gson.fromJson(response.toString(), ApiResponse.class);


                        if(finalTurnList1.get(0).getTurn_number()==1){
                            ApiCount apiCount= finalApi_count_list.get(0);
                            int count_api=apiCount.getApi_count_success_2();
                            apiCount.setApi_count_success_2(count_api+1);
                            finalApi_count_list.set(0,apiCount);

                        }
                        else {
                            ApiCount apiCount= finalApi_count_list.get(0);
                            int count_api=apiCount.getApi_count_success_1();
                            apiCount.setApi_count_success_1(count_api+1);
                            finalApi_count_list.set(0,apiCount);
                        }


                        // Load Forex news items from JSON
                        String readJsonData = StorageUtils.readJsonFromFile(context, FILE_NAME_PENDING);
                        String readJsonData_currencies = StorageUtils.readJsonFromFile(context, FILE_NAME_CURRENCIES_LOCAL);

//        Toast.makeText(getContext(), "Read JSON: " + readJsonData, Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity-Watch_list", apiResponse.toString());


                        // Parse JSON data
                        Type listType = new TypeToken<List<PendingPrice>>() {
                        }.getType();
                        List<PendingPrice> pendingList = new Gson().fromJson(readJsonData, listType);
                        ArrayList<PendingPrice> pendingList_copy = new ArrayList<>(pendingList);

                        Type listType_currencies = new TypeToken<List<ForexCurrency>>() {
                        }.getType();
                        List<ForexCurrency> currencies = new Gson().fromJson(readJsonData_currencies, listType_currencies);
                        ArrayList<ForexCurrency> currencies_copy = new ArrayList<>(currencies);

                        for (int i = 0; i < apiResponse.getQuotes().size(); i++) {
                            Log.d("MainActivity-Qutoes", apiResponse.getQuotes().get(i).toString());
                        }
                        for (int i = 0; i < pendingList.size(); i++) {
                            Log.d("MainActivity-Saved", pendingList.get(i).getPair());
                        }




                            if (pendingList != null || apiResponse.getQuotes() != null) {


                                Calendar calendar = Calendar.getInstance();
                                for (int i = 0; i < apiResponse.getQuotes().size(); i++) {
//                                Log.d("MainActivity-Inside-api", pair);

                                    if (apiResponse.getQuotes().get(i).getInstrument() == null) {
                                        String pair = apiResponse.getQuotes().get(i).getBaseCurrency() + apiResponse.getQuotes().get(i).getQuoteCurrency();
                                        double price = apiResponse.getQuotes().get(i).getMid();
                                        long dateMillis = apiResponse.getTimestamp();


                                        Log.d("MainActivity-Inside-api", pair);
                                        for (int j = 0; j < pendingList.size(); j++) {

                                            if (pendingList.get(j).getPair().equals(pair) && !pendingList.get(j).getFilled().equals("Yes")) {
                                                long dateMillis_ = Long.parseLong(pendingList.get(j).getDate());
                                                double price_ = Double.parseDouble(String.valueOf((pendingList.get(j).getPrice())));


                                                if (pendingList.get(j).getDirection().equals("above")) {
                                                    if (price > price_) {
                                                        pendingList_copy.get(j).setFilled("Yes");
                                                        pendingList_copy.get(j).setDate_filled(String.valueOf(calendar.getTimeInMillis()));

                                                        int prevAlertNumber=currencies.get(pendingList_copy.get(j).getPosFromCurrency()).getAlertNumber();
                                                        currencies.get(pendingList_copy.get(j).getPosFromCurrency()).setAlertNumber(prevAlertNumber-1);

                                                        finalFilled_list.add(pendingList_copy.get(j));
                                                        Log.d("MainActivity-Watch_list", pendingList.get(j).getPair());

                                                        // Create an intent for the stop button
                                                        Intent stopIntent1 = new Intent(context, NotificationActionReceiver.class);
                                                        stopIntent1.setAction("WATCH_LIST_ALERT");
                                                        int id_1= count_notification[0]++;
                                                        Log.d("MainActivity-WATCH", String.valueOf(id_1));

                                                        stopIntent1.putExtra("id",id_1);
                                                        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, id_1, stopIntent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                                        // Create notification
                                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                        String channelId = "Api 10 interval data";
                                                        String channelName = "10 Minute Api Notification";
                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                                            notificationManager.createNotificationChannel(channel);
                                                        }

                                                        Notification notification = new NotificationCompat.Builder(context, channelId)
                                                                .setContentTitle("Watch list [" + pendingList.get(j).getPair() + "]")
                                                                .setContentText(pendingList.get(j).getPair() + " price  moves " + pendingList.get(j).getDirection() + " " + pendingList.get(j).getPrice() + " " + "level")
                                                                .setStyle(new NotificationCompat.BigTextStyle().bigText(pendingList.get(j).getNote())) // For longer text                                                                .setSmallIcon(R.drawable.notif)
                                                                .addAction(R.drawable.ic_baseline_delete_forever_24, "Remove", stopPendingIntent) // Add stop button to notification
                                                                .setOngoing(true) // Make the notification ongoing
                                                                .setSmallIcon(R.drawable.notif)
                                                                .setAutoCancel(false)
                                                                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                                                                .build();

                                                        notificationManager.notify(id_1, notification);
                                                        // Display the response using Log
                                                        Log.d("MainActivity-Api-worker", apiResponse.toString());
                                                    }
                                                } else if (pendingList.get(j).getDirection().equals("below")) {
                                                    if (price < price_) {
                                                        pendingList_copy.get(j).setFilled("Yes");
                                                        pendingList_copy.get(j).setDate_filled(String.valueOf(calendar.getTimeInMillis()));
                                                        int prevAlertNumber=currencies.get(pendingList_copy.get(j).getPosFromCurrency()).getAlertNumber();
                                                        currencies.get(pendingList_copy.get(j).getPosFromCurrency()).setAlertNumber(prevAlertNumber-1);
                                                        finalFilled_list.add(pendingList_copy.get(j));
                                                        Log.d("MainActivity-Watch_list", pendingList.get(j).getPair());

                                                        // Create an intent for the stop button
                                                        Intent stopIntent2 = new Intent(context, NotificationActionReceiver.class);
                                                        stopIntent2.setAction("WATCH_LIST_ALERT");
                                                        int id_=count_notification[0]++;
                                                        Log.d("MainActivity-WATCH", String.valueOf(id_));

                                                        stopIntent2.putExtra("id",id_);
                                                        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, id_, stopIntent2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                                        // Create notification
                                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                        String channelId = "Api 10 interval data";
                                                        String channelName = "10 Minute Api Notification";
                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                                            notificationManager.createNotificationChannel(channel);
                                                        }

                                                        Notification notification = new NotificationCompat.Builder(context, channelId)
                                                                .setContentTitle("Watch list [" + pendingList.get(j).getPair() + "]")
                                                                .setContentText(pendingList.get(j).getPair() + " price  moves " + pendingList.get(j).getDirection() + " " + pendingList.get(j).getPrice() + " " + "level")
                                                                .setStyle(new NotificationCompat.BigTextStyle().bigText(pendingList.get(j).getNote())) // For longer text                                                                .setSmallIcon(R.drawable.notif)
                                                                .addAction(R.drawable.ic_baseline_delete_forever_24, "Remove", stopPendingIntent) // Add stop button to notification
                                                                .setOngoing(true) // Make the notification ongoing
                                                                .setSmallIcon(R.drawable.notif)
                                                                .setAutoCancel(false)
                                                                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                                                                .build();

                                                        notificationManager.notify(id_, notification);
                                                        // Display the response using Log
                                                        Log.d("MainActivity-Api-worker", apiResponse.toString());
                                                    }
                                                }


                                            }


                                        }



                                    } else if (apiResponse.getQuotes().get(i).getInstrument() != null) {
                                        String pair = apiResponse.getQuotes().get(i).getInstrument();
                                        double price = apiResponse.getQuotes().get(i).getMid();
                                        long dateMillis = apiResponse.getTimestamp();


                                        Log.d("MainActivity-Inside-ins", pair);
                                        for (int j = 0; j < pendingList.size(); j++) {

                                            if (pendingList.get(j).getPair().equals(pair) && !pendingList.get(j).getFilled().equals("Yes")) {
                                                long dateMillis_ = Long.parseLong(pendingList.get(j).getDate());
                                                double price_ = Double.parseDouble(String.valueOf((pendingList.get(j).getPrice())));


                                                if (pendingList.get(j).getDirection().equals("above")) {
                                                    if (price > price_) {
                                                        pendingList_copy.get(j).setFilled("Yes");
                                                        pendingList_copy.get(j).setDate_filled(String.valueOf(calendar.getTimeInMillis()));
                                                        int prevAlertNumber=currencies.get(pendingList_copy.get(j).getPosFromCurrency()).getAlertNumber();
                                                        currencies.get(pendingList_copy.get(j).getPosFromCurrency()).setAlertNumber(prevAlertNumber-1);
                                                        finalFilled_list.add(pendingList_copy.get(j));
                                                        Log.d("MainActivity-Watch_list", pendingList.get(j).getPair());
                                                        int id_=count_notification[0]++;
                                                        Log.d("MainActivity-WATCH", String.valueOf(id_));

                                                        // Create an intent for the stop button
                                                        Intent stopIntent = new Intent(context, NotificationActionReceiver.class);
                                                        stopIntent.setAction("WATCH_LIST_ALERT");
                                                        stopIntent.putExtra("id",id_);
                                                        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, id_, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


                                                        // Create notification
                                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                        String channelId = "Api 10 interval data";
                                                        String channelName = "10 Minute Api Notification";
                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                                            notificationManager.createNotificationChannel(channel);
                                                        }

                                                        Notification notification = new NotificationCompat.Builder(context, channelId)
                                                                .setContentTitle("Watch list [" + pendingList.get(j).getPair() + "]")
                                                                .setContentText(pendingList.get(j).getPair() + " price  moves " + pendingList.get(j).getDirection() + " " + pendingList.get(j).getPrice() + " " + "level")
                                                                .setStyle(new NotificationCompat.BigTextStyle().bigText(pendingList.get(j).getNote())) // For longer text
                                                                .setSmallIcon(R.drawable.notif)
                                                                .setAutoCancel(false)
                                                                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                                                                .addAction(R.drawable.ic_baseline_delete_forever_24, "Remove", stopPendingIntent) // Add stop button to notification
                                                                .setOngoing(true) // Make the notification ongoing
                                                                .build();

                                                        notificationManager.notify(id_, notification);
                                                        // Display the response using Log
                                                        Log.d("MainActivity-Api-worker", apiResponse.toString());
                                                    }
                                                } else if (pendingList.get(j).getDirection().equals("below")) {
                                                    if (price < price_) {
                                                        pendingList_copy.get(j).setFilled("Yes");
                                                        pendingList_copy.get(j).setDate_filled(String.valueOf(calendar.getTimeInMillis()));
                                                        int prevAlertNumber=currencies.get(pendingList_copy.get(j).getPosFromCurrency()).getAlertNumber();
                                                        currencies.get(pendingList_copy.get(j).getPosFromCurrency()).setAlertNumber(prevAlertNumber-1);
                                                        finalFilled_list.add(pendingList_copy.get(j));
                                                        Log.d("MainActivity-Watch_list", pendingList.get(j).getPair());

                                                        // Create an intent for the stop button
                                                        Intent stopIntent = new Intent(context, NotificationActionReceiver.class);
                                                        stopIntent.setAction("WATCH_LIST_ALERT");
                                                        int id_=count_notification[0]++;
                                                        stopIntent.putExtra("id",id_);
                                                        Log.d("MainActivity-WATCH", String.valueOf(id_));

                                                        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, id_, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                                        // Create notification
                                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                        String channelId = "Api 10 interval data";
                                                        String channelName = "10 Minute Api Notification";
                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                                                            notificationManager.createNotificationChannel(channel);
                                                        }

                                                        Notification notification = new NotificationCompat.Builder(context, channelId)
                                                                .setContentTitle("Watch list [" + pendingList.get(j).getPair() + "]")
                                                                .setContentText(pendingList.get(j).getPair() + " price  moves " + pendingList.get(j).getDirection() + " " + pendingList.get(j).getPrice() + " " + "level")
                                                                .setStyle(new NotificationCompat.BigTextStyle().bigText(pendingList.get(j).getNote())) // For longer text                                                                .setSmallIcon(R.drawable.notif)
                                                                .addAction(R.drawable.ic_baseline_delete_forever_24, "Remove", stopPendingIntent) // Add stop button to notification
                                                                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                                                                .setSmallIcon(R.drawable.notif)
                                                                .setOngoing(true) // Make the notification ongoing
                                                                .setAutoCancel(false)
                                                                .build();

                                                        notificationManager.notify(id_, notification);
                                                        // Display the response using Log
                                                        Log.d("MainActivity-Api-worker", apiResponse.toString());
                                                    }
                                                }


                                            }

                                        }

                                    }

                                }

                            }else {

                            }


                            boolean isNewNots=count_notification_[0]<count_notification[0];

                        saved_file_api_count(context,finalApi_count_list);

                        Intent intent_api_count= new Intent("android.intent.action.WithInMain_api_count");
                        context.sendBroadcast(intent_api_count);


                        if(isNewNots){
//

                            addNewPending(pendingList_copy, context);
                            String jsonData_turn_list = gson.toJson(finalTurnList);
                            finalNotifcation_list.get(0).setNotification_id_watch_list(count_notification[0]);
                            finalNotifcation_list.get(0).setNotification_Reminder(1);
                            String jsonData_notification_list = gson.toJson(finalNotifcation_list);
                            StorageUtils.writeJsonToFile(context, FILE_NAME_TURN, jsonData_turn_list);
                            StorageUtils.writeJsonToFile(context, FILE_NAME_NOTIFICATION, jsonData_notification_list);
                            String jsonData_filled_list = gson.toJson(finalFilled_list);
                            StorageUtils.writeJsonToFile(context, FILE_NAME_FILLED_LOCAL, jsonData_filled_list);
                            saved_file_currencies(context,currencies_copy);

//                            Alert
                            Intent intent= new Intent("android.intent.action.WithInMain");
                            context.sendBroadcast(intent);
                            ReminderScheduler.scheduleReminder(context);
                        }
                        }

                },
                new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        Log.d("MainActivity-Api-worker", "Error: " + error.getMessage());
                        // Create notification
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        String channelId = "Api 10 interval data";
                        String channelName = "10 Minute Api Notification";

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                            notificationManager.createNotificationChannel(channel);
                        }

                        if(finalTurnList1.get(0).getTurn_number()==1){
                            ApiCount apiCount= finalApi_count_list.get(0);
                            int count_api=apiCount.getApi_count_fail_2();
                            apiCount.setApi_count_fail_2(count_api+1);
                            finalApi_count_list.set(0,apiCount);

                        }
                        else {
                            ApiCount apiCount= finalApi_count_list.get(0);
                            int count_api=apiCount.getApi_count_fail_1();
                            apiCount.setApi_count_fail_1(count_api+1);
                            finalApi_count_list.set(0,apiCount);
                        }

                        saved_file_api_count(context,finalApi_count_list);
                        Intent intent_api_count= new Intent("android.intent.action.WithInMain_api_count");
                        context.sendBroadcast(intent_api_count);



                        Notification notification = new NotificationCompat.Builder(context, channelId)
                                .setContentTitle("Watch list")
                                .setContentText("Failed to get watch list data")
                                .setPriority(MAX_PRIORITY)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Failed to get Api data".toString())) // For longer text
                                .setSmallIcon(R.drawable.notif)
                                .setVibrate(new long[]{ 1000, 1000, 1000, 1000})
                                .build();

                        notificationManager.notify(1, notification);

                    }
                }
        );




                // Add the request to the RequestQueue
                requestQueue.add(jsonObjectRequest);
            }
        }


    }

    private void addNewPending(ArrayList<PendingPrice> list, Context context) {

        ArrayList<PendingPrice> empty_list = new ArrayList<>();
        ArrayList<PendingPrice> final_list = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFilled().equals("Not")){
                final_list.add(list.get(i));
            }
        }

        Gson gson = new Gson();
        String jsonData_ = gson.toJson(empty_list);
        StorageUtils.writeJsonToFile(context, FILE_NAME_PENDING, jsonData_);
        StorageUtils.writeJsonToFile(context, FILE_NAME_PENDING_FOREX_LOCAL, jsonData_);
        StorageUtils.writeJsonToFile(context, FILE_NAME_FILLED_LOCAL, jsonData_);
        StorageUtils.writeJsonToFile(context, FILE_NAME_PENDING_LOCAL, jsonData_);


        String jsonData = gson.toJson(final_list);
        StorageUtils.writeJsonToFile(context, FILE_NAME_PENDING, jsonData);
        StorageUtils.writeJsonToFile(context, FILE_NAME_PENDING_FOREX_LOCAL, jsonData);
        StorageUtils.writeJsonToFile(context, FILE_NAME_FILLED_LOCAL, jsonData);
        StorageUtils.writeJsonToFile(context, FILE_NAME_PENDING_LOCAL, jsonData);


    }


    public static String new_pairs_to_api(ArrayList<String> _list){
        Set<String> linkedHashSet = new LinkedHashSet<>(_list);
        ArrayList<String> pair_to_Add_list=new ArrayList<>();
        int pair_to_Add_num=0;
        pair_to_Add_list.add("NZDJPY");
        pair_to_Add_list.add("AUDJPY");
        pair_to_Add_list.add("GBPAUD");
        pair_to_Add_list.add("GBPNZD");
        pair_to_Add_list.add("AUDUSD");


        try {
            pair_to_Add_num= (int) (4*Math.random());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // Iterating over elements
        String querry_pairs="";
        int last_element=linkedHashSet.size()-1;
        ArrayList<String> unique_list=new ArrayList<>();
        unique_list.addAll(linkedHashSet);
        for (int pair_index = 0; pair_index < linkedHashSet.size(); pair_index++) {
            if(last_element==pair_index){
                querry_pairs+=unique_list.get(pair_index);
            }else {
                querry_pairs+=unique_list.get(pair_index)+",";
            }

        }
        if(pair_to_Add_num%2==0){
            querry_pairs=pair_to_Add_list.get(pair_to_Add_num)+","+querry_pairs;
        }else {
            querry_pairs=querry_pairs+","+pair_to_Add_list.get(pair_to_Add_num);
        }






        return querry_pairs;
    }


    public  void saved_file_currencies(Context context, ArrayList<ForexCurrency> currencies){
        Gson gson = new Gson();
        //CLEAR
        ArrayList<ForexCurrency> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
        StorageUtils.writeJsonToFile(context, FILE_NAME_CURRENCIES_LOCAL, jsonData_);



        // SAVE DATA
        String jsonData = gson.toJson(currencies);

        Log.d("MainActivity-Api-worker", "<<<<<<< " +jsonData+" >>>>>>>");

        StorageUtils.writeJsonToFile(context, FILE_NAME_CURRENCIES_LOCAL, jsonData);
//        TODO:to remove
//        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData);

    }

    public  void saved_file_api_count(Context context, ArrayList<ApiCount> apiCounts){
        Gson gson = new Gson();
        //CLEAR
        ArrayList<ApiCount> clear_list=new ArrayList<>();
        String jsonData_ = gson.toJson(clear_list);
        StorageUtils.writeJsonToFile(context, FILE_NAME_API_COUNT, jsonData_);

        // SAVE DATA
        String jsonData = gson.toJson(apiCounts);
        StorageUtils.writeJsonToFile(context, FILE_NAME_API_COUNT, jsonData);
//        TODO:to remove
//        StorageUtils.writeJsonToFile(getContext(), FILE_NAME_PENDING_LOCAL, jsonData);

    }



}
