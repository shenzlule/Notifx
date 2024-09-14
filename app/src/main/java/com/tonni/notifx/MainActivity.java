package com.tonni.notifx;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonni.notifx.Utils.Storage.StorageUtils;
import com.tonni.notifx.Utils.scheduler.NotificationScheduler;
import com.tonni.notifx.Utils.workers.NewsExpireWorker;
import com.tonni.notifx.Utils.workers.NewsWorker;
import com.tonni.notifx.Utils.workers.setNewsAlarmOnLoadWorker;
import com.tonni.notifx.frags.FilledFragment;
import com.tonni.notifx.frags.ForexFragment;
import com.tonni.notifx.frags.Journal;
import com.tonni.notifx.frags.NewsFragment;
import com.tonni.notifx.frags.WatchFragment;
import com.tonni.notifx.frags.stats.PieChartFrag;
import com.tonni.notifx.inter.MainActivityInterface;
import com.tonni.notifx.inter.MainReturnForexCurrencyInterface;
import com.tonni.notifx.inter.RefreshableFragment;
import com.tonni.notifx.models.PendingPrice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity implements MainActivityInterface, MainReturnForexCurrencyInterface {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;
    private static final String TAG = "ICMP";
    private static final int REQUEST_SET_ALARM = 1;
    private static final int REQUEST_CODE_VIBRATE_PERMISSION = 1;
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM_PERMISSION = 2;
    private static final String FILE_NAME_PENDING_LOCAL = "pending_pending.json";
    private static final String FILE_NAME_PENDING_FOREX_LOCAL = "pending_forex.json";


    private Handler handler;
    private Runnable runnable;
    private ImageView btnSaveJson,btnOpenFolder;


    public PieChartFrag fragmentHistory;
    public NewsFragment fragmentNews;
    public FilledFragment fragmentFilled;
    public WatchFragment fragmentWatch_list;
    public Journal fragmentJournal;
    private ProgressBar progressBar;
    public ForexFragment fragmentPair;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_CODE = 101;

//    Toasty.custom(yourContext, "I'm a custom Toast", yourIconDrawable, tintColor, duration, withIcon,
//    shouldTint).show();
//    Toasty.warning(yourContext, "Beware of the dog.", Toast.LENGTH_SHORT, true).show();
//    Toasty.info(yourContext, "Here is some info for you.", Toast.LENGTH_SHORT, true).show();
//    Toasty.success(yourContext, "Success!", Toast.LENGTH_SHORT, true).show();
//    Toasty.error(yourContext, "This is an error toast.", Toast.LENGTH_SHORT, true).show();


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null) {
                switch (action) {
                    case "android.intent.action.WithInMain":

//                        handler = new Handler(Looper.getMainLooper());
//                        runnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                // Task to be executed every 4 second
//                                btnSaveJson.setVisibility(View.GONE);
//                                btnOpenFolder.setVisibility(View.GONE);
//
//                                progressBar.setVisibility(View.VISIBLE);
//                                // Re-run the runnable after 4 seconds
//                                handler.postDelayed(this, 4000);
//                            }
//                        };
//
//                        // Stop the handler after 4 seconds
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                progressBar.setVisibility(View.GONE);
//                                btnSaveJson.setVisibility(View.VISIBLE);
//                                btnOpenFolder.setVisibility(View.VISIBLE);
//                                handler.removeCallbacks(runnable);
//                            }
//                        }, 4000);
//
//                        // Start the handler
//                        handler.post(runnable);

                        Log.d("MainActivity-Broadcast", "Broadcast Received successfully");
                        Toasty.success(getApplicationContext(), "On watch Success!", Toast.LENGTH_SHORT, true).show();
                        try {
                            fragmentPair.getLocalFile_main();

                        }catch (Exception e){

                        }
                        try {
                            fragmentWatch_list.getLocalFile_main();

                        }catch (Exception e){

                            Toasty.warning(getApplicationContext(), "Watch list Updated", Toast.LENGTH_SHORT, true).show();
                        }
                        try {
                            fragmentFilled.getLocalFile_main();

                        }catch (Exception e){

                            Toasty.warning(getApplicationContext(), "Filled list Updated", Toast.LENGTH_SHORT, true).show();
                        }

                        break;

                    case "android.intent.action.WithInMainNews":
                        Log.d("MainActivity-Broadcast", "Broadcast Received successfully");
                        Toasty.success(getApplicationContext(), "News Updated!", Toast.LENGTH_SHORT, true).show();
                        try {
                            fragmentNews.getLocalFile_main();
                            Toasty.success(getApplicationContext(), "News Updated", Toast.LENGTH_SHORT, true).show();

                        }catch (Exception e){
                            Toasty.warning(getApplicationContext(), "News Updated", Toast.LENGTH_SHORT, true).show();
                        }


                        break;
                    case "android.intent.action.WithInMainNewsFrag":
                        Log.d("MainActivity-Broadcast", "Broadcast Received successfully");
                        try {
//                            fragmentNews.getLocalFile_main();
                            Toasty.success(getApplicationContext(), "Upcoming News Alert!", Toast.LENGTH_SHORT, true).show();

                        }catch (Exception e){
                            Toasty.warning(getApplicationContext(), "Upcoming News Alert!", Toast.LENGTH_SHORT, true).show();
                        }


                        break;

                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                    new String[]{Manifest.permission.SET_ALARM},
                    REQUEST_SET_ALARM);

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NewsWorker.class).build();
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);

        }else {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NewsWorker.class).build();
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
        }




        IntentFilter intentFilter = new IntentFilter("android.intent.action.WithInMain");
        registerReceiver(broadcastReceiver,intentFilter);
        IntentFilter intentFilter1 = new IntentFilter("android.intent.action.WithInMainNews");
        registerReceiver(broadcastReceiver,intentFilter1);







        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fragmentNews=new NewsFragment();
        fragmentPair=new ForexFragment();
        fragmentWatch_list =new WatchFragment();
        fragmentFilled=new FilledFragment();
        fragmentHistory=new PieChartFrag();
        fragmentJournal=new Journal();

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }


        btnSaveJson = toolbar.findViewById(R.id.btnSaveJson);
        btnOpenFolder = toolbar.findViewById(R.id.btnOpenFolder);
        progressBar=toolbar.findViewById(R.id.progress);



        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                // Task to be executed every 4 second
                btnSaveJson.setVisibility(View.GONE);
                btnOpenFolder.setVisibility(View.GONE);

                progressBar.setVisibility(View.VISIBLE);
                // Re-run the runnable after 4 seconds
                handler.postDelayed(this, 4000);
            }
        };

        // Stop the handler after 4 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                btnSaveJson.setVisibility(View.VISIBLE);
                btnOpenFolder.setVisibility(View.VISIBLE);
                handler.removeCallbacks(runnable);
            }
        }, 4000);

        // Start the handler
        handler.post(runnable);

        btnSaveJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


                    String jsonData = StorageUtils.readJsonFromFile(getApplicationContext(), FILE_NAME_PENDING_LOCAL);

                    Type listType = new TypeToken<List<PendingPrice>>() {
                    }.getType();
                    ArrayList<PendingPrice> list = new Gson().fromJson(jsonData, listType);

                    if (list == null || list.size() == 0) {
                        Toasty.error(getApplicationContext(), "Can't backUp null or zero items.", Toast.LENGTH_SHORT, true).show();
                    } else {
                        Log.d("MainActivity", "tapped successfully");
                        String file_name = "pendingBackUp.json";
                        StorageUtils.writeJsonToFile_backUp(MainActivity.this, file_name, jsonData);
                    }


                }else {

                    String jsonData = StorageUtils.readJsonFromFile(getApplicationContext(), FILE_NAME_PENDING_LOCAL);

                    Type listType = new TypeToken<List<PendingPrice>>() {
                    }.getType();
                    ArrayList<PendingPrice> list = new Gson().fromJson(jsonData, listType);

                    if (list == null || list.size() == 0) {
                        Toasty.error(getApplicationContext(), "Can't backUp null or zero items.", Toast.LENGTH_SHORT, true).show();
                    } else {

                        String file_name = "pendingBackUp.json";
                        StorageUtils.writeJsonToFile_backUp(MainActivity.this, file_name, jsonData);
                    }

                }
            }
        });

        btnOpenFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((MainActivity.this) , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


                    if (Build.VERSION.SDK_INT >= 30){
                        if (!Environment.isExternalStorageManager()){
                            Intent getpermission = new Intent();
                            getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivity(getpermission);
                        }
                    }

                    String file_name = "pendingBackUp.json";
                    String readJsonData = StorageUtils.readJsonFromFile_backUp(getApplicationContext(), file_name);

                    Type listType = new TypeToken<List<PendingPrice>>() {
                    }.getType();
                    ArrayList<PendingPrice> list = new Gson().fromJson(readJsonData, listType);

                    if (list == null) {
                        Toasty.error(getApplicationContext(), "File doesn't exist.", Toast.LENGTH_SHORT, true).show();
                    } else if (list.size() == 0) {
                        Toasty.error(getApplicationContext(), "File has zero items.", Toast.LENGTH_SHORT, true).show();
                    } else {
                        Log.d("MainActivity", "tapped successfully");
                        StorageUtils.writeJsonToFile(MainActivity.this, FILE_NAME_PENDING_LOCAL, readJsonData);
                        StorageUtils.writeJsonToFile(MainActivity.this, FILE_NAME_PENDING, readJsonData);
                        StorageUtils.writeJsonToFile(MainActivity.this, FILE_NAME_PENDING_FOREX_LOCAL, readJsonData);
                        Toasty.warning(getApplicationContext(), "BackUp Successfully done.", Toast.LENGTH_SHORT, true).show();
                        try {
                            fragmentPair.getLocalFile_main();

                        } catch (Exception e) {

                        }
                        try {
                            fragmentWatch_list.getLocalFile_main();

                        } catch (Exception e) {

                            Toasty.warning(getApplicationContext(), "Watch list restored.", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                }else {
                    if (Build.VERSION.SDK_INT >= 30){
                        if (!Environment.isExternalStorageManager()){
                            Intent getpermission = new Intent();
                            getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivity(getpermission);

                        }
                    }
                    String file_name = "pendingBackUp.json";
                    String readJsonData = StorageUtils.readJsonFromFile_backUp(getApplicationContext(), file_name);

                    Type listType = new TypeToken<List<PendingPrice>>() {
                    }.getType();
                    ArrayList<PendingPrice> list = new Gson().fromJson(readJsonData, listType);

                    if (list == null) {
                        Toasty.error(getApplicationContext(), "File doesn't exist.", Toast.LENGTH_SHORT, true).show();
                    } else if (list.size() == 0) {
                        Toasty.error(getApplicationContext(), "File has zero items.", Toast.LENGTH_SHORT, true).show();
                    } else {
                        Log.d("MainActivity", "tapped successfully");
                        StorageUtils.writeJsonToFile(MainActivity.this, FILE_NAME_PENDING_LOCAL, readJsonData);
                        StorageUtils.writeJsonToFile(MainActivity.this, FILE_NAME_PENDING, readJsonData);
                        StorageUtils.writeJsonToFile(MainActivity.this, FILE_NAME_PENDING_FOREX_LOCAL, readJsonData);
                        try {
                            fragmentPair.getLocalFile_main();

                        } catch (Exception e) {

                        }
                        try {
                            fragmentWatch_list.getLocalFile_main();

                        } catch (Exception e) {

                            Toasty.warning(getApplicationContext(), "Watch list restored.", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                }
            }
        });




        // Check and request vibration and schedule exact alarm permissions if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.VIBRATE}, REQUEST_CODE_VIBRATE_PERMISSION);
            } else {

            }
        } else {

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM_PERMISSION);
            } else {

            }
        } else {

        }

//


        NotificationScheduler.scheduleNotification(this);
        SetNewsInOnAppOpen();







        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter(this);

        adapter.addFragment(fragmentPair, "Pairs");
        adapter.addFragment(fragmentWatch_list, "Watch list");
        adapter.addFragment(fragmentFilled, "Filled");
        adapter.addFragment(fragmentNews, "News");
        adapter.addFragment(fragmentHistory, "Stats");
        adapter.addFragment(fragmentJournal, "Journal");


        viewPager.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(adapter.getFragmentTitle(position))
        ).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Fragment fragment = adapter.createFragment(position);
                if (fragment instanceof RefreshableFragment) {
                    ((RefreshableFragment) fragment).refresh();
                }
            }
        });

        new Thread(() -> {
            Python py = Python.getInstance();
            PyObject pyObject = py.getModule("myHelper");
            PyObject pyObject1 = pyObject.get("getOne");

//            runOnUiThread(() -> Toast.makeText(MainActivity.this, String.valueOf(pyObject1.call()), Toast.LENGTH_SHORT).show());
        }).start();



        Intent intent_ = new Intent("android.intent.action.OVERLAY_ACTION");
        sendBroadcast(intent_);
        Log.d("OverLay-Received", "OVERLAY SUCCESS");


    }



    @Override
    public void MakeConnThruInter() {

        Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public WatchFragment UpdatePendingMainActivity() {
        return fragmentWatch_list;
    }

    @Override
    public ForexFragment UpdateForexMainActivity() {
        return fragmentPair;
    }


    @Override
    public Fragment getFrag() {
        return fragmentWatch_list;
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
            Log.d("MainActivity", "Added "+title);

        }

        public String getFragmentTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    private static final String FILE_NAME_PENDING = "pending.json";
    private static final String FILE_NAME_NEWS = "forex_events.json";


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        // Remove callbacks to avoid memory leaks
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toasty.success(getApplicationContext(), "File Permision granted!", Toast.LENGTH_SHORT, true).show();


            } else {
                // Permission denied
            }
        }

        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent("android.intent.action.OVERLAY_ACTION");
                    sendBroadcast(intent);
                    Log.d("OverLay-Received", "OVERLAY SUCCESS-request");

                }
            }
        }

    }


    private void SetNewsInOnAppOpen(){
        OneTimeWorkRequest workRequest2 = new OneTimeWorkRequest.Builder(NewsExpireWorker.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(workRequest2);

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(setNewsAlarmOnLoadWorker.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);



    }
}
