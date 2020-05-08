package com.example.keepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScreenBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenBroadcastReceiver";
    private static final String SCREEN_ON = Intent.ACTION_SCREEN_ON;
    private static final String SCREEN_OFF = Intent.ACTION_SCREEN_OFF;
    private static final String USER_PRESENT = Intent.ACTION_USER_PRESENT;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SCREEN_OFF.equals(intent.getAction())) {
            Log.e(TAG, "onReceive SCREEN_OFF");
            Intent onePixel = new Intent(context, OnePixelActivity.class);
            onePixel.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(onePixel);
        } else if (SCREEN_ON.equals(intent.getAction()) || USER_PRESENT.equals(intent.getAction())) {
            Log.e(TAG, "onReceive SCREEN_ON USER_PRESENT");
            Intent finish = new Intent(OnePixelActivity.ACTION_FINISH_ONE_PIXEL);
            context.sendBroadcast(finish);
        }
    }

    public static void register(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        context.registerReceiver(new ScreenBroadcastReceiver(), intentFilter);
    }
}
