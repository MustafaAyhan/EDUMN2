package com.example.mustafa.edumn;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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

        TextView textView = (TextView) findViewById(R.id.startText);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Raleway-Light.ttf");
        textView.setTypeface(custom_font);
    }
}
