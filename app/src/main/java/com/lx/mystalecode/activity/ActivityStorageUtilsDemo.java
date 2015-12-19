package com.lx.mystalecode.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lx.mystalecode.R;
import com.lx.mystalecode.utils.StorageManagerHack;


final public class ActivityStorageUtilsDemo extends BaseActivity implements BaseActivity.SwipeBackImpl {

    private LinearLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_demo);
        mContainer = (LinearLayout) findViewById(R.id.asd_container);
        displayVolumePaths();
        displayVolumeDetail();

    }

    private void displayVolumePaths() {
        String[] paths = StorageManagerHack.getVolumePaths(this);
        if (paths != null) {
            InfoGroup group = new InfoGroup(this, "Volume Path");
            for (String path : paths) {
                String state = StorageManagerHack.getVolumeState(this, path);
                group.addField(path, state);
            }
            group.addToLinearLayout(mContainer);
        }
    }

    private void displayVolumeDetail() {
        StorageManagerHack.RefStorageVolume[] volumes = StorageManagerHack.getVolumeList(this);
        if (volumes != null) {
            for (StorageManagerHack.RefStorageVolume v : volumes) {
                try {
                    v.initAllFields();
                    Log.d("liuxu", "volume info: " + v);
                    InfoGroup group = new InfoGroup(this, v.getDescription(this));
                    String path = v.getPath();
                    group.addField("Mount Path:", path);
                    group.addField("Primary:", v.isPrimary() + "");
                    group.addField("Emulated:", v.isEmulated() + "");
                    group.addField("Removable:", v.isRemovable() + "");
                    group.addField("Allow Mass Storage:", v.allowMassStorage() + "");
                    group.addField("Mtp Reserve Space:", v.getMtpReserveSpace() + "");
                    group.addField("Max File Size:", v.getMaxFileSize() + "");
                    group.addToLinearLayout(mContainer);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private static class InfoGroup extends LinearLayout {

        private static final int TEXT_PADDING = 20;
        private static final int ITEM_MARGIN = 60;
        private static final int FIELD_MARGIN = 30;
        private static final int NAME_VALUE_MARGIN = 3;

        public InfoGroup(Context context, String title) {
            super(context);
            this.setOrientation(VERTICAL);
            this.setGravity(Gravity.CENTER_HORIZONTAL);
            addTitle(title);
        }

        private void addTitle(String title) {
            TextView tv = new TextView(getContext());
            tv.setTextColor(Color.WHITE);
            tv.setPadding(TEXT_PADDING, TEXT_PADDING, TEXT_PADDING, TEXT_PADDING);
            tv.setText(title);
            LayoutParams llp = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            addViewInLayout(tv, 0, llp);
        }

        public void addField(String name, String value) {
            TextView tvName = new TextView(getContext());
            tvName.setBackgroundColor(Color.WHITE);
            tvName.setTextColor(Color.BLACK);
            tvName.setPadding(TEXT_PADDING, TEXT_PADDING, TEXT_PADDING, TEXT_PADDING);
            tvName.setText(name);
            LayoutParams llpName = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            llpName.setMargins(0, FIELD_MARGIN, 0, NAME_VALUE_MARGIN);
            addViewInLayout(tvName, -1, llpName);

            TextView tvValue = new TextView(getContext());
            tvValue.setBackgroundColor(Color.WHITE);
            tvValue.setTextColor(Color.BLACK);
            tvValue.setPadding(TEXT_PADDING, TEXT_PADDING, TEXT_PADDING, TEXT_PADDING);
            tvValue.setText(value);
            LayoutParams llpValue = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            llpValue.setMargins(0, 0, 0, 0);
            addViewInLayout(tvValue, -1, llpValue);
        }

        public void addField(String value) {
            TextView tvValue = new TextView(getContext());
            tvValue.setBackgroundColor(Color.WHITE);
            tvValue.setTextColor(Color.BLACK);
            tvValue.setPadding(TEXT_PADDING, TEXT_PADDING, TEXT_PADDING, TEXT_PADDING);
            tvValue.setText(value);
            LayoutParams llpValue = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            llpValue.setMargins(0, FIELD_MARGIN, 0, 0);
            addViewInLayout(tvValue, -1, llpValue);
        }

        public void addToLinearLayout(LinearLayout container) {
            LayoutParams llp = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 0, 0, ITEM_MARGIN);
            container.addView(this, llp);
        }
    }
}
