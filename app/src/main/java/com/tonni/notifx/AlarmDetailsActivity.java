package com.tonni.notifx;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmDetailsActivity extends AppCompatActivity {

    private Vibrator vibrator;
    private TextView txtMarquue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        // Get the alarm details from the intent
        String alarmDetails = getIntent().getStringExtra("alarmDetails");
        TextView detailsTextView = findViewById(R.id.alarm_details_text_view);
        txtMarquue=findViewById(R.id.txtTicker);
        txtMarquue.setSelected(true);

        detailsTextView.setText(alarmDetails);

        // Vibrate with a pattern: vibrate for 1 second, pause for 1 second, for 1 minute
        long[] pattern = {0, 1000, 1000}; // Start immediately, vibrate for 1 second, pause for 1 second
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(pattern, 0); // Repeat the pattern indefinitely (0 means start at the beginning of the array)
        }

        // Set up the cancel button
        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            if (vibrator != null) {
                vibrator.cancel();
            }
            finish();
        });

        // Stop the vibration after 1 minute (60 seconds)
        new android.os.Handler().postDelayed(() -> {
            if (vibrator != null) {
                vibrator.cancel();
            }
            finish();
        }, 30000); // 30 seconds
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}