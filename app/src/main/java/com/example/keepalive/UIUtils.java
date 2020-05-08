package com.example.keepalive;

import android.content.Context;
import android.os.PowerManager;

import java.lang.reflect.Method;

public final class UIUtils {
    private UIUtils() {

    }

    public static boolean checkScreenOn(Context context) {
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            Method method = powerManager != null ? powerManager.getClass().getMethod("isScreenOn", new Class[0]) : null;
            return (Boolean) (method != null ? method.invoke(powerManager, new Object[0]) : Boolean.valueOf(false));
        } catch (Exception e) {
            return false;
        }
    }
}
