package com.tonni.notifx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tonni.notifx.Utils.scheduler.NotificationScheduler;
import com.tonni.notifx.frags.FilledFragment;
import com.tonni.notifx.frags.ForexFragment;
import com.tonni.notifx.frags.HistoryFragment;
import com.tonni.notifx.frags.NewsFragment;
import com.tonni.notifx.frags.PendingFragment;
import com.tonni.notifx.inter.MainActivityInterface;
import com.tonni.notifx.inter.RefreshableFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MainActivityInterface {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;
    private static final String TAG = "ICMP";
    private static final int REQUEST_SET_ALARM = 1;
    private static final int REQUEST_CODE_VIBRATE_PERMISSION = 1;
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM_PERMISSION = 2;
    public Fragment fragmentPair,fragmentNews,fragmentPending,fragmentFilled,fragmentHistory;

    private static final String URL = "https://marketdata.tradermade.com/api/v1/live";
    private static final String CURRENCY = "USDJPY,GBPUSD,USA30";
    private static final String API_KEY = "HvHNPZP8zjXZuRYwAj-S";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentNews=new NewsFragment();
        fragmentPair=new ForexFragment();
        fragmentPending=new PendingFragment();
        fragmentFilled=new FilledFragment();
        fragmentHistory=new HistoryFragment();


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







        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter(this);

        adapter.addFragment(fragmentPair, "Pairs");
        adapter.addFragment(fragmentNews, "News");
        adapter.addFragment(fragmentPending, "Watch");
        adapter.addFragment(fragmentHistory, "Api");
        adapter.addFragment(fragmentFilled, "Filled");


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






    }



    @Override
    public void MakeConnThruInter() {

        Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
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
        }

        public String getFragmentTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }






}
