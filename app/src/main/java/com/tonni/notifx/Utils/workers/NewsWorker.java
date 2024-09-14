package com.tonni.notifx.Utils.workers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.Utils.Icmp4a;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.Utils.receivers.AlertReceiver;
import com.tonni.notifx.models.ForexNewsItem;
import com.tonni.notifx.models.NewsOrganised;
import com.tonni.notifx.models.WeekNumberModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewsWorker extends Worker {
    private Context context;

    private static final String TAG = "ICMP";
    private static final String FILE_NAME = "forex_events.json";
    private static final String FILE_NAME_NEWS_ORGANISED = "forex_events_.json";

    public NewsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        fetchNews();
        return Result.success();
    }

    private void fetchNews() {
               MakeConnection();

    }


    public void MakeConnection() {

        // Get the current week number
        int currentWeekNumber = getCurrentWeekNumber();
        List<WeekNumberModel> weekItems = new ArrayList<>();
        List<WeekNumberModel> weekItems_ = new ArrayList<>();
        weekItems.add(new WeekNumberModel(currentWeekNumber));

        Icmp4a icmp = new Icmp4a();
        String host = "google.com";
        try {
            Icmp4a.Status status = icmp.ping("8.8.8.8");
            Icmp4a.PingResult result = status.result;
            if (result instanceof Icmp4a.PingResult.Success) {
                Icmp4a.PingResult.Success success = (Icmp4a.PingResult.Success) result;

                // Load Forex news items from JSON
                String readJsonData = StorageUtils.readJsonFromFile(context, "week_number.json");
//                Toast.makeText(this, "Read JSON: " + readJsonData, Toast.LENGTH_SHORT).show();
                // Parse JSON data
                Type listType = new TypeToken<List<WeekNumberModel>>() {
                }.getType();
                weekItems_ = new Gson().fromJson(readJsonData, listType);

                // Load Forex news items from JSON
                String readJsonData_2 = StorageUtils.readJsonFromFile(context, FILE_NAME);
                try{
                    Log.d("NewsFragment", "Read JSON: " + readJsonData_2);
                }catch (Exception e){
                    Log.d("NewsFragment", "Read JSON: " + " Exception Null Null");
                }


                // Parse JSON data
                Type listType_ = new TypeToken<List<ForexNewsItem>>() {
                }.getType();
                List<ForexNewsItem> checkifNullOrEmpty = new Gson().fromJson(readJsonData_2, listType_);

                if (checkifNullOrEmpty == null || checkifNullOrEmpty.size() == 0) {
                    saveWeekNumber(weekItems);
                    // Execute the scraping task
                    doInBackground();
                    Log.d(TAG, "Data retrieved because null array  or empty items");
                    Intent intent= new Intent("android.intent.action.WithInMainNews");
                    context.sendBroadcast(intent);

//                    Toast.makeText(context, "Item list  is empty trying connect to host", Toast.LENGTH_SHORT).show();
                    saveWeekNumber(weekItems);
//                    Toast.makeText(this, "Current Week Number saved: " + String.valueOf(currentWeekNumber), Toast.LENGTH_SHORT).show();

                } else {

                    if (weekItems_ == null) {
                        saveWeekNumber(weekItems);
                        // Execute the scraping task
                        doInBackground();
                        Log.d(TAG, "Data retrieved");
                        Intent intent= new Intent("android.intent.action.WithInMainNews");
                        context.sendBroadcast(intent);

//                    Toast.makeText(this, "Live internet connection-null", Toast.LENGTH_SHORT).show();
                        saveWeekNumber(weekItems);
//                    Toast.makeText(this, "Current Week Number saved: " + String.valueOf(currentWeekNumber), Toast.LENGTH_SHORT).show();
                    } else {
                        if (!String.valueOf(weekItems.get(0).getWeek_number()).equals(String.valueOf(weekItems_.get(0).getWeek_number()))) {
                            // Execute the scraping task
                            doWork();
                            Intent intent= new Intent("android.intent.action.WithInMainNews");
                            context.sendBroadcast(intent);

                            Log.d(TAG, "Data retrieved");
//                        Toast.makeText(this, "Live internet connection--not null", Toast.LENGTH_SHORT).show();
                            saveWeekNumber(weekItems);
//                    Toast.makeText(this, "Current Week Number saved: " + String.valueOf(currentWeekNumber), Toast.LENGTH_SHORT).show();

                        } else {
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
//            Toast.makeText(context, "No live internet connection", Toast.LENGTH_SHORT).show();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }



    private List<ForexNewsItem> extractTableData(Document doc) {
        List<ForexNewsItem> newsItems = new ArrayList<>();
        long id_count=5000;
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
                ForexNewsItem newsItem = new ForexNewsItem(id_count,date +"-"+ time, event, currency, impactColor, isDone,isAlarm);
                if (impactColor.equals("red") || impactColor.equals("ora")||impactColor.equals("yel")){



                    newsItems.add(newsItem);
                    id_count=id_count+20;

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
        StorageUtils.writeJsonToFile(context, "week_number.json", jsonData);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarmForNewsItem(ForexNewsItem item, Context context) throws ParseException {
        Calendar calendar = item.getCalendar();
        Calendar nowCalendar=Calendar.getInstance();


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            long longItemTime=item.getCalendar().getTimeInMillis();
            long twoHour=longItemTime-(120*60*1000);
            long oneHour=longItemTime-(60*60*1000);
            long halfHour=longItemTime-(30*60*1000);
            long min_15_=longItemTime-(15*60*1000);
            long min_5_=longItemTime-(5*60*1000);
            long min_2_=longItemTime-(2*60*1000);
            long twoHour_=(120*60*1000);
            long oneHour_=(60*60*1000);
            long halfHour_=(30*60*1000);
            long min_15__=(15*60*1000);
            long min_5__=(5*60*1000);
            long min_2__=(2*60*1000);
            Calendar min_2_Calendar=Calendar.getInstance();
            Calendar min_5_Calendar=Calendar.getInstance();
            Calendar min_15_Calendar=Calendar.getInstance();
            Calendar halfHourCalendar=Calendar.getInstance();
            Calendar oneHourCalendar=Calendar.getInstance();
            Calendar twoHourCalendar=Calendar.getInstance();


            twoHourCalendar.setTimeInMillis(twoHour);
            oneHourCalendar.setTimeInMillis(oneHour);
            halfHourCalendar.setTimeInMillis(halfHour);
            min_15_Calendar.setTimeInMillis(min_15_);
            min_5_Calendar.setTimeInMillis(min_5_);
            min_2_Calendar.setTimeInMillis(min_2_);

            if(item.getCalendar().getTime().after(nowCalendar.getTime())){

                int id=(int) item.getId();
                Intent intent_main = new Intent(context, AlertReceiver.class);
                intent_main.putExtra("name", item.getName()+"[Now]");
                intent_main.putExtra("time", item.getTime());
                intent_main.putExtra("id", id);

                PendingIntent pendingIntent_now = PendingIntent.getBroadcast(context, id, intent_main, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, item.getCalendar().getTimeInMillis(), pendingIntent_now);
                Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getCalendar().getTimeInMillis());
                Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[Now]" + " " + item.getCalendar().getTimeInMillis());

                if(item.getCalendar().getTime().after(twoHourCalendar.getTime()) &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=twoHour_){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 2hrs]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+1);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, twoHourCalendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + twoHourCalendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[Two hour]" + " " + twoHourCalendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(oneHourCalendar.getTime())  &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=oneHour_){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 1hr]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+2);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+2, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, oneHourCalendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + oneHourCalendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[One hour]" + " " + oneHourCalendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(halfHourCalendar.getTime())  &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=halfHour_){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 30min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+3);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+3, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, halfHourCalendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + halfHourCalendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[30min]" + " " + halfHourCalendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(min_15_Calendar.getTime()) &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=min_15__){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 15min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+4);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+4, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, min_15_Calendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + min_15_Calendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[15 min]" + " " + min_15_Calendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(min_5_Calendar.getTime())  &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=min_5__){

                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 5min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+5);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+5, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, min_5_Calendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + min_5_Calendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[15 min]" + " " + min_5_Calendar.getTimeInMillis());
                }
                if(item.getCalendar().getTime().after(min_2_Calendar.getTime())  &&
                        (item.getCalendar().getTimeInMillis()-nowCalendar.getTimeInMillis())>=min_2__){
                    Intent intent = new Intent(context, AlertReceiver.class);
                    intent.putExtra("name", item.getName()+"[In 2min]");
                    intent.putExtra("time", item.getTime());
                    intent.putExtra("id", id+6);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id+6, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, min_2_Calendar.getTimeInMillis(), pendingIntent);
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + min_2_Calendar.getTimeInMillis());
                    Log.d("Boot Notifx", "Alarm set for " + item.getName() + " at " + item.getTime()+"[15 min]" + " " + min_2_Calendar.getTimeInMillis());

                }


            }

        } else {
            Log.d("Boot Notifx", "AlarmManager is null");
        }
    }


    private List<ForexNewsItem> doInBackground() {
        List<ForexNewsItem> newsItems = new ArrayList<>();
        String url = "https://www.forexfactory.com/calendar";
        try {
            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
            newsItems = extractTableData(doc);
            onPostExecute(newsItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsItems;
    }

    private void onPostExecute(List<ForexNewsItem> newsItems) {
            // Convert to JSON
            Calendar currentCalendar = Calendar.getInstance();
            JSONArray jsonArray = new JSONArray();
            ArrayList<NewsOrganised> newsOrganised =new ArrayList<>();
            newsOrganised.add(new NewsOrganised(new ArrayList<ForexNewsItem>(),
                                                new ArrayList<ForexNewsItem>(),
                                                new ArrayList<ForexNewsItem>(),
                                                new ArrayList<ForexNewsItem>(),
                                                new ArrayList<ForexNewsItem>(),
                                                new ArrayList<ForexNewsItem>(),
                                                new ArrayList<ForexNewsItem>()));

            for (ForexNewsItem item : newsItems) {
                JSONObject jsonObject = new JSONObject();

                try {
                    Calendar calendar = item.getCalendar();
                    int DOW = calendar.get(Calendar.DAY_OF_WEEK);

                    if(DOW==1){
                        newsOrganised.get(0).getSundayNews().add(item);
                    }else  if(DOW==2){
                        newsOrganised.get(0).getMondayNews().add(item);
                    }else  if(DOW==3){
                        newsOrganised.get(0).getTuesdayNews().add(item);
                    }else  if(DOW==4){
                        newsOrganised.get(0).getWednesdayNews().add(item);
                    }else  if(DOW==5){
                        newsOrganised.get(0).getThursdayNews().add(item);
                    }else  if(DOW==6){
                        newsOrganised.get(0).getFridayNews().add(item);
                    }else  if(DOW==7){
                        newsOrganised.get(0).getSaturdayNews().add(item);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    Calendar itemCalendar = item.getCalendar();
                    if (itemCalendar != null && itemCalendar.after(currentCalendar)) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            setAlarmForNewsItem(item,context);
//                        }
                        item.setIsAlarm("Yes");
                    }else {
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

        StringBuilder result = new StringBuilder();
        result.append("Results: ").append("\n");

        for (NewsOrganised newsOrganised_ : newsOrganised) {

            for (ForexNewsItem forexNewsItem : newsOrganised_.getSundayNews()){
                result.append("Sunday: ").append(forexNewsItem.getName()).append("\n");

            }
            for (ForexNewsItem forexNewsItem : newsOrganised_.getMondayNews()){
                result.append("Monday: ").append(forexNewsItem.getName()).append("\n");

            }
            for (ForexNewsItem forexNewsItem : newsOrganised_.getTuesdayNews()){
                result.append("Tuesday: ").append(forexNewsItem.getName()).append("\n");

            }
            for (ForexNewsItem forexNewsItem : newsOrganised_.getWednesdayNews()){
                result.append("wednesday: ").append(forexNewsItem.getName()).append("\n");

            }
            for (ForexNewsItem forexNewsItem : newsOrganised_.getThursdayNews()){
                result.append("Thursday: ").append(forexNewsItem.getName()).append("\n");

            }
            for (ForexNewsItem forexNewsItem : newsOrganised_.getFridayNews()){
                result.append("Friday: ").append(forexNewsItem.getName()).append("\n");

            }
            for (ForexNewsItem forexNewsItem : newsOrganised_.getSaturdayNews()){
                result.append("Saturday: ").append(forexNewsItem.getName()).append("\n");

            }



            Log.d("Results TAG:===>",result.toString());


        }


            String jsonData = jsonArray.toString();
            // save to local
            Gson gson = new Gson();
            String jsonData2 = gson.toJson(newsOrganised);
            //convert the to array

            // Save JSON data to file
            StorageUtils.writeJsonToFile(context, FILE_NAME, jsonData);
            StorageUtils.writeJsonToFile(context, FILE_NAME_NEWS_ORGANISED, jsonData2);

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(setNewsAlarmOnLoadWorker.class).build();
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);




        }



}
