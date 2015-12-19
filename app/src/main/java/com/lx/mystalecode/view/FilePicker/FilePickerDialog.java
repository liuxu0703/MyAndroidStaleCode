package com.lx.mystalecode.view.FilePicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * author: liuxu
 * date: 2014-10-29
 *
 * wrap functions of FilePickerList into an AlertDialog.
 * use FileSelectCallback to handle the result.
 * use FilePickerDialog.Builder to easy create the dialog.
 *
 * Demo: ActivityFilePickerDemo
 *       ActivityPlayViewDemo
 */
public class FilePickerDialog extends AlertDialog
        implements OnClickListener, FilePickerList.FilePickerCallback {

    private FilePickerList.FilePickerFilter mFilter;
    private String mFileRoot;
    private FilePickerView mFilePickerView;
    private FileSelectCallback mCallback;

    protected FilePickerDialog(
            Context context,
            String root,
            FilePickerList.FilePickerFilter filter,
            FileSelectCallback callback) {
        super(context);
        mFileRoot = root;
        mFilter = filter;
        mCallback = callback;
        this.setCancelable(false);
        initButton();
        initFilePickerView();
    }

    private void initFilePickerView() {
        mFilePickerView = new FilePickerView(
                getContext(), mFileRoot, mFilter);
        mFilePickerView.getFilePickerList().setFilePickerCallback(this);
        this.setView(mFilePickerView);
    }

    private void initButton() {
        Resources res = getContext().getResources();
        String positive = res.getString(android.R.string.ok);
        String negative = res.getString(android.R.string.cancel);
        this.setButton(DialogInterface.BUTTON_POSITIVE, positive, this);
        this.setButton(DialogInterface.BUTTON_NEGATIVE, negative, this);
    }

    private void updatePositiveButtonEnabled() {
        String selected = mFilePickerView.getCurrentSelection();
        boolean enabled = (selected != null);
        Button positiveButton = getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setEnabled(enabled);
    }

    @Override
    public void show() {
        super.show();
        mFilePickerView.refresh();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            if (mCallback != null) {
                mCallback.onFileSelected(
                        mFilePickerView.getCurrentSelection());
            }
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            if (mCallback != null) {
                mCallback.onFileSelected(null);
            }
            break;
        }
    }

    @Override
    public void onBrowseFolder(String folder) {
        mFilePickerView.displayCurrentFolder(folder);
        updatePositiveButtonEnabled();
    }

    @Override
    public void onFileSelected(String file) {
        updatePositiveButtonEnabled();
    }

    private static class FilePickerView extends LinearLayout {

        private static final int CURRENT_DIR_VIEW_HEIGHT = 35; // in dp
        private static final int CURRENT_DIR_VIEW_PADDING = 5; // in dp

        private FilePickerList mFilePickerList;
        private TextView mCurDirView;
        private String mFileRoot;
        private FilePickerList.FilePickerFilter mFilter;

        public FilePickerView(
                Context context,
                String fileRoot,
                FilePickerList.FilePickerFilter filter) {
            super(context);
            this.setOrientation(LinearLayout.VERTICAL);
            mFileRoot = fileRoot;
            mFilter = filter;
            initView();
        }

        public String getCurrentSelection() {
            return mFilePickerList.getCurrentSelection();
        }

        private void initView() {
            addCurrentDirView();
            addFilePickerList();
        }

        // add a TextView to display the folder we are currently browsing
        private void addCurrentDirView() {
            int height = dip2px(getContext(), CURRENT_DIR_VIEW_HEIGHT);
            int padding = dip2px(getContext(), CURRENT_DIR_VIEW_PADDING);

            mCurDirView = new TextView(getContext());
            mCurDirView.setGravity(Gravity.CENTER_VERTICAL);
            mCurDirView.setSingleLine();
            mCurDirView.setEllipsize(TruncateAt.MARQUEE);
            mCurDirView.setFocusable(true);
            mCurDirView.setFocusableInTouchMode(true);
            mCurDirView.setBackgroundColor(Color.GRAY);
            mCurDirView.setPadding(padding, 0, padding, 0);

            LayoutParams params =
                    new LayoutParams(
                            LayoutParams.MATCH_PARENT, height);
            this.addView(mCurDirView, -1, params);
        }

        private void addFilePickerList() {
            mFilePickerList = new FilePickerList(getContext());
            mFilePickerList.setFilter(mFilter);
            mFilePickerList.setRoot(mFileRoot);
            mFilePickerList.enableBackAsHeader();

            LayoutParams params =
                    new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            getListHeightAsDialog(getContext()));
            this.addView(mFilePickerList, -1, params);
        }

        private void displayCurrentFolder(String folder) {
            mCurDirView.setText(folder);
        }

        private void refresh() {
            mFilePickerList.refresh();
        }

        private FilePickerList getFilePickerList() {
            return mFilePickerList;
        }

        // set the height of the list to be half of the screen height
        private static int getListHeightAsDialog(Context context) {
            int screenHeight;
            WindowManager wm = (WindowManager)
                    context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metric = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metric);
            screenHeight = metric.heightPixels;
            return screenHeight / 2;
        }

        private static int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }
    }

    /**
     * builder for FilePickerDialog.
     */
    public static class Builder {

        private Context mContext;
        private FilePickerList.FilePickerFilter mFilter;
        private String mFileRoot;
        private FileSelectCallback mCallback;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * indicate from which folder we begin browse.
         * the browse operation will be limited to this folder and its
         * child folder.
         * @param root
         */
        public Builder setRoot(String root) {
            mFileRoot = root;
            return this;
        }

        /**
         * set a filter to indicate what you want to display, and what you
         * want to accept as result.
         * @param filter
         *            see FilePickerFilter for details
         */
        public Builder setFilePickerFilter(FilePickerList.FilePickerFilter filter) {
            mFilter = filter;
            return this;
        }

        /**
         * set a callback to handle the result.
         * the callback will be invoked when the dialog is dismissed.
         * @param callback
         *            see FileSelectCallback for details
         */
        public Builder setFileSelectCallback(FileSelectCallback callback) {
            mCallback = callback;
            return this;
        }

        /**
         * create the dialog
         */
        public FilePickerDialog create() {
            FilePickerDialog dlg = new FilePickerDialog(
                    mContext, mFileRoot, mFilter, mCallback);
            return dlg;
        }
    }

    /**
     * when a button is clicked, be it the positive button or
     * negative button, this callback will be invoked, and the
     * dialog is dismissed.
     */
    public interface FileSelectCallback {

        /**
         * if negative button is clicked, the selected file would be null.
         * if positive button is clicked, the selected file is passed by
         * the argument.
         */
        public void onFileSelected(String file);
    }

}
