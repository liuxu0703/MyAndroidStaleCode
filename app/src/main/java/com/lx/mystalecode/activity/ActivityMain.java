package com.lx.mystalecode.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lx.mystalecode.R;

import java.util.ArrayList;

public class ActivityMain extends Activity {

    public final static ArrayList<DemoButtonData> BUTTON_DATA_LIST =
            new ArrayList<>();

    static {
        BUTTON_DATA_LIST.add(new DemoButtonData("File Picker",
                ActivityFilePickerDemo.class));
        BUTTON_DATA_LIST.add(new DemoButtonData("StorageManagerHack",
                ActivityStorageUtilsDemo.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout buttonContainer = (LinearLayout) this
                .findViewById(R.id.main_container);
        for (DemoButtonData data : BUTTON_DATA_LIST) {
            DemoButton button = new DemoButton(this, data);
            DemoButton.addToLinearLayout(buttonContainer, button);
        }
    }

    private static class DemoButton extends Button implements View.OnClickListener {

        private final static int MARGIN = 10;

        private final Context mContext;
        private final DemoButtonData mData;

        public DemoButton(Context context, DemoButtonData data) {
            super(context);
            mContext = context;
            mData = data;
            initView();
        }

        private void initView() {
            setTag(mData.mTitle); // so that we could find it later
            setText(mData.mTitle);
            setBackgroundResource(R.drawable.btn_action);
            setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, mData.mActivity);
            mContext.startActivity(intent);
        }

        public static void addToLinearLayout(LinearLayout parent, DemoButton button) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
            parent.addView(button, llp);
        }
    }

    @SuppressWarnings("rawtypes")
    private static class DemoButtonData {

        String mTitle;
        Class mActivity;

        DemoButtonData(String title, Class activity) {
            mTitle = title;
            mActivity = activity;
        }
    }

}
