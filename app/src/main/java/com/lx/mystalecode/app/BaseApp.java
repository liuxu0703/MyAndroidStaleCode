package com.lx.mystalecode.app;

import android.app.Application;

import com.lx.mystalecode.utils.AlertUtils;
import com.lx.mystalecode.utils.GlobalThreadManager;
import com.lx.mystalecode.utils.ScreenUtils;

public class BaseApp extends Application {

    private static Application sInstance;

    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        GlobalThreadManager.init(this);
        AlertUtils.init(this);
        ScreenUtils.init(this);
    }

}
