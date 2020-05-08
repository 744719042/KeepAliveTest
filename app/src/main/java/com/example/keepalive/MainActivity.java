package com.example.keepalive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.keepalive.connection.LocalService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WatchDog.getInstance().startWatch();
    }

    public void exitApp(View view) {
        finish();
        System.exit(0);
    }

    public void bindRemote(View view) {
        Intent intent = new Intent(this, LocalService.class);
        startService(intent);
    }

    public void launchOnePixel(View view) {
        Intent intent = new Intent(this, OnePixelActivity.class);
        startActivity(intent);
    }
}
