package com.tonni.notifx.Utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.tonni.notifx.R;

import java.util.Calendar;

public class OverlayReceiver extends BroadcastReceiver {

    private WindowManager windowManager;
    private View overlayView;
    private Vibrator vibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("OverLay-Received", "OVERLAY SUCCESS-main");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent settingsIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(settingsIntent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showOverlay(context);
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showOverlay(context);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showOverlay(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        overlayView = inflater.inflate(R.layout.overlay_layout, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        Button closeButton = overlayView.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(v -> removeOverlay());

        windowManager.addView(overlayView, params);
        startVibration(context);
    }

    private void removeOverlay() {
        if (overlayView != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
            stopVibration();
        }
    }

    private void startVibration(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {1000, 1000}; // 1 second on, 1 second off
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
            }
            new Handler().postDelayed(this::stopVibration, 60000); // Stop after 1 minute
        }
    }

    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
