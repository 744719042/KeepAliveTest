package com.example.keepalive;

public class WatchDog {
    static {
        System.loadLibrary("native-lib");
    }

    private WatchDog() {

    }

    private static class WatchDogHolder {
        private final static WatchDog INSTANCE = new WatchDog();
    }

    public static WatchDog getInstance() {
        return WatchDogHolder.INSTANCE;
    }

    public void startWatch() {
        startChild("" + android.os.Process.myUid());
    }

    private native void startChild(String userId);
}
