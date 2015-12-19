package com.lx.mystalecode.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lx.mystalecode.utils.ScreenUtils;
import com.lx.mystalecode.view.SwipeBack.SwipeBackLayout;

/**
 * author: liuxu
 * date: 2015-02-06
 *
 * activity base
 * TODO: double click finish
 */
public abstract class BaseActivity extends FragmentActivity {

    protected String TAG;

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        super.onCreate(savedInstanceState);
        if (this instanceof SwipeBackImpl) {
            initSwipeBack();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (this instanceof SwipeBackImpl) {
            mSwipeBackLayout.attachToActivity(this);
        }
    }

    public void startActivity(Class cls) {
        startActivity(new Intent(BaseActivity.this, cls));
    }

    // ======================================
    // about swipe back

    private void initSwipeBack() {
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = new SwipeBackLayout(this);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(ScreenUtils.getScreenWidth());
    }

    /**
     * by implementing this interface, sub class of BaseActivity will get the ability
     * of "finish activity by swiping back screen".
     * NOTE: AppTheme should be set properly in AndroidManifest.xml.
     */
    public interface SwipeBackImpl {
    }

}
