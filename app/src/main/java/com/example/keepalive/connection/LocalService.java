package com.example.keepalive.connection;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.keepalive.IConnect;

import androidx.annotation.Nullable;

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private boolean firstStart = true;
    public static final String ARG_REBOOT = "arg_reboot";

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (firstStart) {
            connectRemote();
            firstStart = false;
        }
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return new IConnect.Stub() {
            @Override
            public void connect() throws RemoteException {
                Log.i(TAG, "Local Stub");
            }
        };
    }

    private void connectRemote() {
        Intent intent = new Intent(this, RemoteService.class);
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "Connected to RemoteService");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "RemoteService disconnected");
                connectRemote();
            }
        }, BIND_IMPORTANT);
    }
}
