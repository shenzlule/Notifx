package com.tonni.notifx;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tonni.notifx.Utils.Error;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        TextView error_btn=findViewById(R.id.error_txt);
        error_btn.setText(Error.get_Error());
    }
}