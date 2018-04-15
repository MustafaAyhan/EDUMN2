package com.example.mustafa.edumn.CustomClasses;

import android.app.Application;
import android.content.Context;

/**
 * Created by Mustafa on 10.03.2018.
 */

public class MyApplication extends Application {

    private static Context context;

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }
}