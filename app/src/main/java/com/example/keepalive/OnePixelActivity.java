package com.example.keepalive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class OnePixelActivity extends AppCompatActivity {
    private static final String TAG = "OnePixelActivity";
    public static final String ACTION_FINISH_ONE_PIXEL = "action_finish_one_pixel";
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = 1;
        layoutParams.height = 1;
        layoutParams.x = 0;
        layoutParams.y = 0;
        window.setAttributes(layoutParams);

        IntentFilter intentFilter = new IntentFilter(ACTION_FINISH_ONE_PIXEL);
        registerReceiver(receiver = new OnePixelReceiver(), intentFilter);

        if (UIUtils.checkScreenOn(this)) {
            finish();
        }
        Log.e(TAG, "onCreate");
    }

    private class OnePixelReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive");
            if (ACTION_FINISH_ONE_PIXEL.equals(intent.getAction())) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        unregisterReceiver(receiver);
    }
}
