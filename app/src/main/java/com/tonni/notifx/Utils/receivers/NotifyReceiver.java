package com.tonni.notifx.Utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.tonni.notifx.Utils.workers.FetchWorker;
import com.tonni.notifx.Utils.workers.NotifyWorker;

public class NotifyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotifyWorker.class).build();
//        WorkManager.getInstance(context).enqueue(workRequest);
        OneTimeWorkRequest fetch_workRequest = new OneTimeWorkRequest.Builder(FetchWorker.class).build();
        WorkManager.getInstance(context).enqueue(fetch_workRequest);
    }


}


