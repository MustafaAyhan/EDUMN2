package com.example.mustafa.edumn;

/**
 * Created by Mustafa on 21.03.2018.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FullScreenImage extends Activity {
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.image_full_layout);

        Bundle extras = getIntent().getExtras();
        Bitmap bmp = extras.getParcelable("imagebitmap");

        ImageView imgDisplay;
        Button btnClose;


        imgDisplay = findViewById(R.id.imgDisplay);
        btnClose = findViewById(R.id.btnClose);


        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FullScreenImage.this.finish();
            }
        });


        imgDisplay.setImageBitmap(bmp);

    }


}