package com.example.mustafa.edumn;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new CountDownTimer(3000, 1000) {
            public void onFinish() {
                // When timer is finished
                // Execute your code here
                startActivity(new Intent(StartActivity.this, WelcomeActivity.class));
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();
    }
}
