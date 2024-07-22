package com.tonni.notifx.Utils.workers;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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
import com.tonni.notifx.api.ApiResponse;
import com.tonni.notifx.models.ApiTurn;
import com.tonni.notifx.models.PendingPrice;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FetchWorker extends Worker {

    private static final String URL = "https://marketdata.tradermade.com/api/v1/live";
    private static final String URL2 = "https://marketdata.tradermade.com/api/v1/live";
    private static final String CURRENCY = "USDJPY,GBPUSD,USA30,GBPUSD,EURUSD,XAUUSD,USDCAD,USDCHF";
    private static final String API_KEY = "HvHNPZP8zjXZuRYwAj-S";
    private static final String API_KEY2 = "HvHNPZP8zjXZuRYwAj-S";

    private static final String FILE_NAME_PENDING = "pending.json";
    private static final String FILE_NAME_TURN = "turnApi.json";
    private static final int MAX_PRIORITY = 10;

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

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String queryUrl = URL + "?currency=" + CURRENCY + "&api_key=" + API_KEY;

        //get turn
        // Read JSON data from internal storage
        String readJsonData_turn = StorageUtils.readJsonFromFile(context, FILE_NAME_TURN);
        // Parse JSON data
        Type listType_turn = new TypeToken<List<ApiTurn>>() {
        }.getType();
        List<ApiTurn> turnList = new Gson().fromJson(readJsonData_turn, listType_turn);

        if (turnList==null){
            turnList=new ArrayList<>();
            turnList.add(0,new ApiTurn(1));
        }else if(turnList.get(0).getTurn_number()== 1){
            //make use of one api and flip for the next turn
            turnList.add(0,new ApiTurn(2));
            queryUrl = URL + "?currency=" + CURRENCY + "&api_key=" + API_KEY;
        }else if(turnList.get(0).getTurn_number() == 2){
            //make use of one api and flip for the next turn
            turnList.add(0,new ApiTurn(1));
            queryUrl = URL + "?currency=" + CURRENCY + "&api_key=" + API_KEY2;
        }




        final String[] myString = {"No response  from API"};

        List<ApiTurn> finalTurnList = turnList;
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


                        // Load Forex news items from JSON
                        String readJsonData = StorageUtils.readJsonFromFile(context, FILE_NAME_PENDING);
//        Toast.makeText(getContext(), "Read JSON: " + readJsonData, Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity-Watch_list", apiResponse.toString());


                        // Parse JSON data
                        Type listType = new TypeToken<List<PendingPrice>>() {
                        }.getType();
                        List<PendingPrice> pendingList = new Gson().fromJson(readJsonData, listType);
                        List<PendingPrice> pendingList_copy = new ArrayList<>(pendingList);

                        for (int i = 0; i < apiResponse.getQuotes().size(); i++) {
                            Log.d("MainActivity-Qutoes", apiResponse.getQuotes().get(i).toString());
                        }
                        for (int i = 0; i < pendingList.size(); i++) {
                            Log.d("MainActivity-Saved", pendingList.get(i).getPair());
                        }


                        if (pendingList != null || apiResponse.getQuotes() != null) {



                            Calendar calendar = Calendar.getInstance();
                            for (int i = 0; i < apiResponse.getQuotes().size(); i++) {

                                if (apiResponse.getQuotes().get(i).getInstrument().equals("null")) {
                                    String pair = apiResponse.getQuotes().get(i).getBaseCurrency() + apiResponse.getQuotes().get(i).getQuoteCurrency();
                                    double price = apiResponse.getQuotes().get(i).getMid();
                                    long dateMillis = apiResponse.getTimestamp();


                                    Log.d("MainActivity-Inside-api", pair);
                                    for (int j = 0; j < pendingList.size(); j++) {

                                        if (pendingList.get(j).getPair().equals(pair) && !pendingList.get(j).equals("Yes")) {
                                            long dateMillis_ = Long.parseLong(pendingList.get(j).getDate());
                                            double price_ = Double.parseDouble(String.valueOf((pendingList.get(j).getPrice())));


                                            if (pendingList.get(j).getDirection().equals("above")) {
                                                if (price >= price_) {
                                                    pendingList_copy.get(j).setFilled("Yes");
                                                    pendingList_copy.get(j).setDate_filled(String.valueOf(calendar.getTimeInMillis()));
                                                    Log.d("MainActivity-Watch_list", pendingList.get(j).getPair());

                                                    // Create notification
                                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                    String channelId = "Api 10 interval data";
                                                    String channelName = "10 Minute Api Notification";
                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                                                        notificationManager.createNotificationChannel(channel);
                                                    }

                                                    Notification notification = new NotificationCompat.Builder(context, channelId)
                                                            .setContentTitle("Watch list")
                                                            .setContentText(pendingList.get(j).getPair())
                                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(pendingList.get(j).getPair() + " price to moves " + pendingList.get(j).getDirection() + " " + pendingList.get(j).getPrice() + " " + "level")) // For longer text
                                                            .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                                                            .build();

                                                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                                                    // Display the response using Log
                                                    Log.d("MainActivity-Api-worker", apiResponse.toString());
                                                }
                                            } else if (pendingList.get(j).getDirection().equals("below") && !pendingList.get(j).equals("Yes")) {
                                                if (price <= price_) {
                                                    pendingList_copy.get(j).setFilled("Yes");
                                                    pendingList_copy.get(j).setDate_filled(String.valueOf(calendar.getTimeInMillis()));
                                                    Log.d("MainActivity-Watch_list", pendingList.get(j).getPair());

                                                    // Create notification
                                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                    String channelId = "Api 10 interval data";
                                                    String channelName = "10 Minute Api Notification";
                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                                                        notificationManager.createNotificationChannel(channel);
                                                    }

                                                    Notification notification = new NotificationCompat.Builder(context, channelId)
                                                            .setContentTitle("Watch list")
                                                            .setContentText(pendingList.get(j).getPair())
                                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(pendingList.get(j).getPair() + " price to moves " + pendingList.get(j).getDirection() + " " + pendingList.get(j).getPrice() + " " + "level")) // For longer text
                                                            .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                                                            .build();

                                                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                                                    // Display the response using Log
                                                    Log.d("MainActivity-Api-worker", apiResponse.toString());
                                                }
                                            }


                                        }

                                    }

                                }
                                else if (!apiResponse.getQuotes().get(i).getInstrument().equals("null")) {
                                    String pair = apiResponse.getQuotes().get(i).getInstrument();
                                    double price = apiResponse.getQuotes().get(i).getMid();
                                    long dateMillis = apiResponse.getTimestamp();


                                    Log.d("MainActivity-Inside-ins", pair);
                                    for (int j = 0; j < pendingList.size(); j++) {

                                        if (pendingList.get(j).getPair().equals(pair) && !pendingList.get(j).equals("Yes")) {
                                            long dateMillis_ = Long.parseLong(pendingList.get(j).getDate());
                                            double price_ = Double.parseDouble(String.valueOf((pendingList.get(j).getPrice())));


                                            if (pendingList.get(j).getDirection().equals("above")) {
                                                if (price >= price_) {
                                                    pendingList_copy.get(j).setFilled("Yes");
                                                    pendingList_copy.get(j).setDate_filled(String.valueOf(calendar.getTimeInMillis()));
                                                    Log.d("MainActivity-Watch_list", pendingList.get(j).getPair());

                                                    // Create notification
                                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                    String channelId = "Api 10 interval data";
                                                    String channelName = "10 Minute Api Notification";
                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                                                        notificationManager.createNotificationChannel(channel);
                                                    }

                                                    Notification notification = new NotificationCompat.Builder(context, channelId)
                                                            .setContentTitle("Watch list")
                                                            .setContentText(pendingList.get(j).getPair())
                                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(pendingList.get(j).getPair() + " price to moves " + pendingList.get(j).getDirection() + " " + pendingList.get(j).getPrice() + " " + "level")) // For longer text
                                                            .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                                                            .build();

                                                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                                                    // Display the response using Log
                                                    Log.d("MainActivity-Api-worker", apiResponse.toString());
                                                }
                                            } else if (pendingList.get(j).getDirection().equals("below") && !pendingList.get(j).equals("Yes")) {
                                                if (price <= price_) {
                                                    pendingList_copy.get(j).setFilled("Yes");
                                                    pendingList_copy.get(j).setDate_filled(String.valueOf(calendar.getTimeInMillis()));
                                                    Log.d("MainActivity-Watch_list", pendingList.get(j).getPair());

                                                    // Create notification
                                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                                    String channelId = "Api 10 interval data";
                                                    String channelName = "10 Minute Api Notification";
                                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                                                        notificationManager.createNotificationChannel(channel);
                                                    }

                                                    Notification notification = new NotificationCompat.Builder(context, channelId)
                                                            .setContentTitle("Watch list")
                                                            .setContentText(pendingList.get(j).getPair())
                                                            .setStyle(new NotificationCompat.BigTextStyle().bigText(pendingList.get(j).getPair() + " price to moves " + pendingList.get(j).getDirection() + " " + pendingList.get(j).getPrice() + " " + "level")) // For longer text
                                                            .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                                                            .build();

                                                    notificationManager.notify((int) System.currentTimeMillis(), notification);
                                                    // Display the response using Log
                                                    Log.d("MainActivity-Api-worker", apiResponse.toString());
                                                }
                                            }


                                        }

                                    }

                                }

                            }

                        }


                        addNewPending(pendingList_copy, context);
                        String jsonData_turn_list = gson.toJson(finalTurnList);
                        StorageUtils.writeJsonToFile(context, FILE_NAME_TURN, jsonData_turn_list);

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
                            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                            notificationManager.createNotificationChannel(channel);
                        }

                        Notification notification = new NotificationCompat.Builder(context, channelId)
                                .setContentTitle("Watch list")
                                .setContentText("Failed to get watch list data")
                                .setPriority(MAX_PRIORITY)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Failed to get Api data".toString())) // For longer text
                                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                                .build();

                        notificationManager.notify((int) System.currentTimeMillis(), notification);

                    }
                }
        );

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);


    }

    private void addNewPending(List<PendingPrice> list, Context context) {

        List<PendingPrice> empty_list = new ArrayList<>();

        Gson gson = new Gson();
        String jsonData_ = gson.toJson(empty_list);
        StorageUtils.writeJsonToFile(context, FILE_NAME_PENDING, jsonData_);

        String jsonData = gson.toJson(list);
        StorageUtils.writeJsonToFile(context, FILE_NAME_PENDING, jsonData);


    }

}
