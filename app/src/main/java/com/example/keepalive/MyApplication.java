package com.example.keepalive;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenBroadcastReceiver.register(this);
    }
}
