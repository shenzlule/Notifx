package com.tonni.notifx.frags;

import static com.tonni.notifx.Utils.Storage.StorageUtils.writeJsonToFile;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.tonni.notifx.R;
import com.tonni.notifx.Utils.receivers.AlarmReceiver;
import com.tonni.notifx.Utils.Icmp4a;
import com.tonni.notifx.Utils.Storage.StorageUtils;

import com.tonni.notifx.models.TrackRefresh;
import com.tonni.notifx.adapter.ForexNewsAdapter;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.ForexNewsItem;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.tonni.notifx.models.WeekNumberModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewsFragment extends Fragment implements RefreshableFragment {


    private static final String TAG = "ICMP";
    private static final int REQUEST_SET_ALARM = 1;
    ArrayList<ForexNewsItem> validItems;

    private static final String FILE_NAME = "forex_events.json";
    private ArrayList<ForexNewsItem> forexNewsItems;
    private ForexNewsAdapter forexNewsAdapter;
    private RecyclerView recyclerView;
    private String readJsonData;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);




        forexNewsItems=new ArrayList<>();


        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewForexNews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Set adapter
        forexNewsAdapter = new ForexNewsAdapter(forexNewsItems,getContext());
        recyclerView.setAdapter(forexNewsAdapter);

        if(!getLocalFile(forexNewsItems)){
            MakeConnection();
        }





        return view;
    }

    private  void readFine(){

        // Filter out items with old dates
        validItems = new ArrayList<>();
        Calendar currentCalendar = Calendar.getInstance();

        for (int i = 0; i < forexNewsItems.size(); i++) {
            ForexNewsItem item=forexNewsItems.get(i);
            try {
                Calendar itemCalendar = item.getCalendar();
                if (itemCalendar != null && itemCalendar.after(currentCalendar)) {
                    validItems.add(item);
                }else {
                    forexNewsAdapter.notifyItemRemoved(i);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        forexNewsItems.clear();
        forexNewsItems.addAll(validItems);
        forexNewsAdapter.notifyDataSetChanged();



    }



    @Override
    public void refresh() {
        Log.d("NewsFragment", "Refreshing data...");

    }

    private class FetchForexData extends AsyncTask<Void, Void, List<ForexNewsItem>> {

        @Override
        protected List<ForexNewsItem> doInBackground(Void... voids) {
            List<ForexNewsItem> newsItems = new ArrayList<>();
            String url = "https://www.forexfactory.com/calendar";
            try {
                Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
                newsItems = extractTableData(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newsItems;
        }

        @Override
        protected void onPostExecute(List<ForexNewsItem> newsItems) {

            // Check and request permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getContext(),
                            new String[]{Manifest.permission.SET_ALARM},
                            REQUEST_SET_ALARM);
                    return; // Exit the method and wait for permission result
                }
                // Proceed with setting alarms if permission is granted
                // Convert to JSON
                JSONArray jsonArray = new JSONArray();
                Calendar currentCalendar = Calendar.getInstance();
                for (ForexNewsItem item : newsItems) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        Calendar itemCalendar = item.getCalendar();
                        if (itemCalendar != null && itemCalendar.after(currentCalendar)) {
                            setAlarmForNewsItem(item);
                            item.setIsAlarm("Yes");
                        }else {
//                    Toast.makeText(getContext(), "Not after"+String.valueOf(itemCalendar), Toast.LENGTH_SHORT).show();
                            item.setIs_done("Yes");
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject.put("time", item.getTime());
                        jsonObject.put("name", item.getName());
                        jsonObject.put("currency", item.getCurrency());
                        jsonObject.put("status", item.getStatus());
                        jsonObject.put("isDone", item.getIs_done());
                        jsonObject.put("isAlarm", item.getIsAlarm());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String jsonData = jsonArray.toString();

                // Save JSON data to file
                writeJsonToFile(getContext(), FILE_NAME, jsonData);

            } else {
                // Convert to JSON
                Calendar currentCalendar = Calendar.getInstance();
                JSONArray jsonArray = new JSONArray();
                for (ForexNewsItem item : newsItems) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        Calendar itemCalendar = item.getCalendar();
                        if (itemCalendar != null && itemCalendar.after(currentCalendar)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                setAlarmForNewsItem(item);
                            }
                            item.setIsAlarm("Yes");
                        }else {
//                    Toast.makeText(getContext(), "Not after"+String.valueOf(itemCalendar), Toast.LENGTH_SHORT).show();
                            item.setIs_done("Yes");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject.put("time", item.getTime());
                        jsonObject.put("name", item.getName());
                        jsonObject.put("currency", item.getCurrency());
                        jsonObject.put("status", item.getStatus());
                        jsonObject.put("isDone", item.getIs_done());
                        jsonObject.put("isAlarm", item.getIsAlarm());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String jsonData = jsonArray.toString();

                // Save JSON data to file
                StorageUtils.writeJsonToFile(getContext(), FILE_NAME, jsonData);


                new Thread(() -> {

                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "News events downloaded", Toast.LENGTH_SHORT).show());
                    getActivity().runOnUiThread(() -> getLocalFile(forexNewsItems));
                }).start();

            }

        }
    }

    private List<ForexNewsItem> extractTableData(Document doc) {
        List<ForexNewsItem> newsItems = new ArrayList<>();
//        Elements rows = doc.select("tr.calendar_row");
        Element table = doc.selectFirst(".calendar__table");

        String html=table.toString();
        Document doc_ = Jsoup.parse(html);
        Elements rows = doc_.select("tr.calendar__row");


        String dateTracker = "";
        String timeTracker = "";
        for (Element row : rows) {
            Elements cells = row.select("td");

            if (cells.size() > 6) {
                // Extracting data
                String date = row.select("td.calendar__date").text();
                if (date.equals("")){
                    date=dateTracker;

                }else {
                    dateTracker=date;
                }
                String time = row.select("td.calendar__time").text();
                if (time.equals("")){
                    time=timeTracker;

                }else {
                    timeTracker=time;
                }
                String sub = row.select("td.calendar__sub").text();
                Element impact = row.selectFirst(".calendar__impact > span");
                String event = row.select("td.calendar__event").text();
                String detail = row.select("td.calendar__detail").text();
                String actual = row.select("td.calendar__actual").text();
                String forecast = row.select("td.calendar__forecast").text();
                String previous = row.select("td.calendar__previous").text();
                String graph = row.select("td.calendar__graph").text();
                String currency = row.select("td.calendar__currency").text();

                String impactColor = "";
                if (impact != null) {
                    for (String className : impact.classNames()) {
                        if (className.startsWith("icon--ff-impact-")) {
                            impactColor = className.replace("icon--ff-impact-", ""); // Extracting the color part (yel, red, org)
                            break;
                        }
                    }
                }

                // Creating ForexNewsItem object
                String isDone = "Not";
                String isAlarm = "Not";
                ForexNewsItem newsItem = new ForexNewsItem(date +"-"+ time, event, currency, impactColor, isDone,isAlarm);
                if (impactColor.equals("red") || impactColor.equals("ora")||impactColor.equals("yel")){

                    newsItems.add(newsItem);
                }

            } else if (cells.size() == 1) {
                dateTracker = row.select("td.calendar__date").text();
            }
        }


        return newsItems;
    }


    // Method to get the current week number
    private int getCurrentWeekNumber() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private void saveWeekNumber(List<WeekNumberModel> weekItems){
        // Convert to JSON
        JSONArray jsonArray = new JSONArray();
        for (WeekNumberModel item : weekItems) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("week_number", item.getWeek_number());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String jsonData = jsonArray.toString();

        // Save JSON data to file
        StorageUtils.writeJsonToFile(getContext(), "week_number.json", jsonData);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarmForNewsItem(ForexNewsItem item) throws ParseException {
        Calendar calendar = item.getCalendar();

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("name", item.getName());
        intent.putExtra("time", item.getTime());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("MainActivity", "Alarm set for " + item.getName() + " at " + calendar.getTimeInMillis());
            Log.d("MainActivity", "Alarm set for " + item.getName() + " at " + item.getTime()+" "+calendar.getTimeInMillis());

        } else {
            Log.d("MainActivity", "AlarmManager is null");
        }
    }




    public void MakeConnection(){

        // Get the current week number
        int currentWeekNumber = getCurrentWeekNumber();
        List<WeekNumberModel> weekItems =new ArrayList<>();
        List<WeekNumberModel> weekItems_ =new ArrayList<>();
        weekItems.add(new WeekNumberModel(currentWeekNumber));

        Icmp4a icmp = new Icmp4a();
        String host = "google.com";
        try {
            Icmp4a.Status status = icmp.ping("8.8.8.8");
            Icmp4a.PingResult result = status.result;
            if (result instanceof Icmp4a.PingResult.Success) {
                Icmp4a.PingResult.Success success = (Icmp4a.PingResult.Success) result;

                // Load Forex news items from JSON
                String readJsonData = StorageUtils.readJsonFromFile(getContext(), "week_number.json");
//                Toast.makeText(this, "Read JSON: " + readJsonData, Toast.LENGTH_SHORT).show();
                // Parse JSON data
                Type listType = new TypeToken<List<WeekNumberModel>>() {}.getType();
                weekItems_ = new Gson().fromJson(readJsonData, listType);

                // Load Forex news items from JSON
                String readJsonData_2 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME);
                Log.d("NewsFragment", "Read JSON: " + readJsonData);

                // Parse JSON data
                Type listType_ = new TypeToken<List<ForexNewsItem>>() {}.getType();
                List<ForexNewsItem> checkifNullOrEmpty = new Gson().fromJson(readJsonData, listType_);

                if (checkifNullOrEmpty ==null  || checkifNullOrEmpty.size()==0){
                    saveWeekNumber(weekItems);
                    // Execute the scraping task
                    new FetchForexData().execute();
                    Log.d(TAG, "Data retrieved because null array  or empty items");

                    Toast.makeText(getContext(), "Item list  is empty trying connect to host", Toast.LENGTH_SHORT).show();
                    saveWeekNumber(weekItems);
//                    Toast.makeText(this, "Current Week Number saved: " + String.valueOf(currentWeekNumber), Toast.LENGTH_SHORT).show();

                }else {

                    if (weekItems_==null){
                        saveWeekNumber(weekItems);
                        // Execute the scraping task
                        new FetchForexData().execute();
                        Log.d(TAG, "Data retrieved");

//                    Toast.makeText(this, "Live internet connection-null", Toast.LENGTH_SHORT).show();
                        saveWeekNumber(weekItems);
//                    Toast.makeText(this, "Current Week Number saved: " + String.valueOf(currentWeekNumber), Toast.LENGTH_SHORT).show();
                    }else{
                        if (!String.valueOf(weekItems.get(0).getWeek_number()).equals(String.valueOf(weekItems_.get(0).getWeek_number()))){
                            // Execute the scraping task
                            new FetchForexData().execute();
                            Log.d(TAG, "Data retrieved");
//                        Toast.makeText(this, "Live internet connection--not null", Toast.LENGTH_SHORT).show();
                            saveWeekNumber(weekItems);
//                    Toast.makeText(this, "Current Week Number saved: " + String.valueOf(currentWeekNumber), Toast.LENGTH_SHORT).show();

                        }else{
//                    Toast.makeText(this, "Week match " + String.valueOf(currentWeekNumber), Toast.LENGTH_SHORT).show();
                        }

                    }
                }








            } else if (result instanceof Icmp4a.PingResult.Failed) {
                Icmp4a.PingResult.Failed failed = (Icmp4a.PingResult.Failed) result;
                Log.d(TAG, host + "(" + status.ip.getHostAddress() + ") Failed: " + failed.message);
//                Toast.makeText(getContext(), "Failed to get connection with host.", Toast.LENGTH_SHORT).show();
            }
        } catch (Icmp4a.UnknownHostException e) {
            Log.d(TAG, "Unknown host " + host);
            Toast.makeText(getContext(), "No live internet connection", Toast.LENGTH_SHORT).show();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }



    public boolean getLocalFile(ArrayList<ForexNewsItem> newsItemsList){
        Boolean isBol=null;
        // Load  items from JSON
        String readJsonData1 = StorageUtils.readJsonFromFile(getContext(), FILE_NAME);
        // Parse JSON data
        Type listType2 = new TypeToken<List<ForexNewsItem>>() {}.getType();
        ArrayList<ForexNewsItem> newsItems=new Gson().fromJson(readJsonData1, listType2);



        if(newsItems==null){
            newsItems=new ArrayList<ForexNewsItem>();
            // save to local
            Gson gson = new Gson();
            String jsonData = gson.toJson(newsItems);
            StorageUtils.writeJsonToFile(getContext(), FILE_NAME, jsonData);
            isBol=false;
        }else {
            newsItemsList.addAll(newsItems);
            forexNewsAdapter.notifyDataSetChanged();
            readFine();
            isBol= true;

        }
        return isBol;
    }


}
