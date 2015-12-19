package com.lx.mystalecode.utils;

import android.app.Application;
import android.widget.Toast;

/**
 * Created by liuxu on 15-3-19.
 * toast and message dialog
 */
public final class AlertUtils {

    private static Application sApp;

    private AlertUtils() {}

    public static void init(Application app) {
        sApp = app;
    }

    public static void showToastLong(int resId) {
        showToastLong(sApp.getString(resId));
    }

    public static void showToastLong(String msg) {
        showToast(msg, Toast.LENGTH_LONG);
    }

    public static void showToastShort(int resId) {
        showToastShort(sApp.getString(resId));
    }

    public static void showToastShort(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    private static void showToast(final String msg, final int duration) {
        GlobalThreadManager.runInUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(sApp, msg, duration).show();
            }
        });
    }

}
