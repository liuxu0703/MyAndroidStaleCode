package com.lx.mystalecode.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * author: lx
 * date: 15-02-02
 * operations about screen info.
 */
public final class ScreenUtils {

    private static DisplayMetrics sMetric = new DisplayMetrics();
    private static int sStatusBarHeight;

    public static void init(Application app) {
        WindowManager wm = (WindowManager) app.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(sMetric);
    }

    /**
     * convert sp to px
     */
    public static int sp2px(float spValue) {
        final float fontScale = sMetric.scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float fontScale = sMetric.scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * convert dip to px
     */
    public static int dip2px(float dip) {
        return (int) Math.ceil(dip * sMetric.density);
    }

    /**
     * convert px to dip
     */
    public static float px2dip(int px) {
        if (sMetric.density > 0) {
            return px / sMetric.density;
        } else {
            return px;
        }
    }

    /**
     * get screen width, in pixel.
     * @return screen width
     */
    public static int getScreenWidth() {
        return sMetric.widthPixels;
    }

    /**
     * get screen height, in pixel.
     * @return screen height
     */
    public static int getScreenHeight() {
        return sMetric.heightPixels;
    }

    /**
     * get screen density.
     * @return screen density
     */
    public static float getScreenDensity() {
        return sMetric.density;
    }

    /**
     * get activity height, which is the screen height minus status bar height
     * @param activity must be instance of activity
     * @return activity height
     */
    public static int getActivityHeight(Activity activity) {
        return getScreenHeight() - getStatusBarHeight(activity);
    }

    /**
     * get status bar height
     * @param activity must be instance of activity
     * @return status bar height
     */
    public static int getStatusBarHeight(Activity activity) {
        if (sStatusBarHeight == 0) {
            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            sStatusBarHeight = rect.top;
            if (sStatusBarHeight == 0) {
                Class<?> cls;
                try {
                    cls = Class.forName("com.android.internal.R$dimen");
                    Object localObject = cls.newInstance();
                    String sbh = cls.getField("status_bar_height").get(localObject).toString();
                    int i5 = Integer.parseInt(sbh);
                    sStatusBarHeight = activity.getResources().getDimensionPixelSize(i5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return sStatusBarHeight;
    }
}
