package com.example.keepalive.connection;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.example.keepalive.IConnect;
import com.example.keepalive.MainActivity;

public class RemoteService extends Service {
    private static final String TAG = "RemoteService";
    private boolean firstBind = true;
    private Handler handler = new Handler(Looper.getMainLooper());

    public RemoteService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        if (firstBind) {
            connectLocal(false);
            firstBind = false;
        }
        return new IConnect.Stub() {
            @Override
            public void connect() throws RemoteException {
                Log.i(TAG, "Remote Stub");
            }
        };
    }

    private void connectLocal(boolean reboot) {
        Intent intent = new Intent(this, LocalService.class);
        if (reboot) intent.putExtra(LocalService.ARG_REBOOT, true);
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "Connected to LocalService");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "LocalService disconnected");
                connectLocal(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);
                        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(activityIntent);
                    }
                },1500);
            }
        }, BIND_IMPORTANT);
    }
}
