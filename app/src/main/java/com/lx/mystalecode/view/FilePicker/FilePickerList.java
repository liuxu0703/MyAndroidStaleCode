package com.lx.mystalecode.view.FilePicker;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lx.mystalecode.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * author: liuxu
 * date: 2014-10-29
 *
 * a list view to be used as file browser.
 * use enableBackAsHeader() to add a header view to browse back,
 * or use backToParentFolder() to handle the browse back by yourself.
 *
 * call getCurrentSelection() to retrieve the selected file path.
 *
 * you may find FilePickerCallback useful if you want to insert this
 * view to other ViewGroup.
 *
 * use FilePickerFilter to filter files.
 *
 * you can also use FilePickerDialog to quick setup a simple file browser.
 * see FilePickerDialog for details.
 *
 * Demo: ActivityFilePickerDemo
 */
public class FilePickerList extends ListView
        implements OnItemClickListener {

    public static final File DEFAULT_FILE_ROOT =
            Environment.getExternalStorageDirectory();

    // TODO:
    // this class file is independent to other files except for
    // these three icon. maybe we should draw the icon by code instead.
    private static final int DEFAULT_FOLDER_ICON = R.mipmap.ic_file_picker_folder;
    private static final int DEFAULT_FILE_ICON = R.mipmap.ic_file_picker_file;
    private static final int DEFAULT_BACK_ICON = R.mipmap.ic_file_picker_back;

    private static final int ITEM_BKG_COLOR_NORMAL = Color.TRANSPARENT;
    private static final int ITEM_BKG_COLOR_SELECTED = Color.GRAY;

    private static final int ITEM_HEIGHT = 35; // in dp
    private static final int ITEM_ICON_PADDING = 5; // in dp

    private File mFileRoot = DEFAULT_FILE_ROOT;
    private File mFileSelected;
    private File mCurrentFolder;
    private FilePickerFilter mFilter;
    private FileAdapter mAdapter;

    private ItemView mBackAsHeader;
    private boolean mBackAsHeaderAdded = false;
    private FilePickerCallback mCallback;

    public FilePickerList(Context context) {
        super(context);
        initView();
    }

    public FilePickerList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FilePickerList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * get the current selected file
     * @return the selected file. can be null.
     */
    public String getCurrentSelection() {
        if (mFileSelected != null) {
            return mFileSelected.getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * get the folder we are currently browsing.
     * @return the current displayed folder
     */
    public String getCurrentFolder() {
        return mCurrentFolder.getAbsolutePath();
    }

    /**
     * set a filter to indicate what you want to display, and what you
     * want to accept as result.
     * @param filter
     *            see FilePickerFilter for details
     */
    public void setFilter(FilePickerFilter filter) {
        mFilter = filter;
    }

    /**
     * indicate from which folder we begin browse.
     * the browse operation will be limited to this folder and its
     * child folder.
     * @param root
     */
    public void setRoot(String root) {
        if (root == null) {
            return;
        }
        File rootFile = new File(root);
        if (!rootFile.isDirectory()) {
            throw new IllegalArgumentException("root is not a dir: " + root);
        }
        mFileRoot = rootFile;
    }

    /**
     * add callback to be informed when browse to a new folder, or a
     * new file is selected.
     * @param callback
     *            see FilePickerCallback for details
     */
    public void setFilePickerCallback(FilePickerCallback callback) {
        mCallback = callback;
    }

    /**
     * call this to add a header view to this ListView.
     * the header view will serve as a "browse back" button.
     */
    public void enableBackAsHeader() {
        if (!mBackAsHeaderAdded) {
            mBackAsHeader = new ItemView(getContext());
            mBackAsHeader.mIcon.setImageResource(DEFAULT_BACK_ICON);
            mBackAsHeader.mName.setText("Back");
            addHeaderView(mBackAsHeader);
            mBackAsHeaderAdded = true;
        }
    }

    /**
     * when ready, call this method to begin browse
     */
    public void refresh() {
        if (mCurrentFolder == null) {
            mCurrentFolder = mFileRoot;
        }
        displayFolder(mCurrentFolder);
    }

    /**
     * browse to the parent folder
     */
    public void backToParentFolder() {
        if (mCurrentFolder == null || mCurrentFolder.equals(mFileRoot)) {
            return;
        }
        File parent = mCurrentFolder.getParentFile();
        displayFolder(parent);
    }

    private void initView() {
        setOnItemClickListener(this);
    }

    // return true if the file is accepted
    private boolean setFileSelected(File file) {
        FilePickerFilter filter = mFilter == null ?
                FILTER_NORMAL_ALL : mFilter;
        if (filter.canBeSelected(file)) {
            mFileSelected = file;
            if (mCallback != null) {
                mCallback.onFileSelected(mFileSelected.getAbsolutePath());
            }
            return true;
        } else {
            return false;
        }
    }

    private void displayFolder(File folder) {
        try {
            mAdapter = new FileAdapter(getContext(), folder, mFilter);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        mCurrentFolder = folder;
        // the folder may be accepted, so try setFileSelected()
        if (!setFileSelected(mCurrentFolder)) {
            // when browse to a new folder, and the folder is not
            // accepted as selected, then the selected file should
            // be cleared
            mFileSelected = null;
        }

        if (mCallback != null) {
            mCallback.onBrowseFolder(folder.getAbsolutePath());
        }
        setAdapter(mAdapter);
    }

    private void setItemSelected(int id, View selectedView) {
        if (mAdapter == null) {
            return;
        }

        // high light the newly selected item
        int oldId = mAdapter.getSelectedid();
        if (oldId != -1) {
            View oldView = this.findViewById(oldId);
            if (oldView != null) {
                oldView.setBackgroundColor(ITEM_BKG_COLOR_NORMAL);
            }
        }
        selectedView.setBackgroundColor(ITEM_BKG_COLOR_SELECTED);
        mAdapter.setSelectedId(id);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mBackAsHeaderAdded) {
            if (position == 0) {
                // header view "back" is clicked
                backToParentFolder();
                return;
            } else {
                // position is migrated by header view. change before use
                position -= 1;
            }
        }
        File file;
        if (mAdapter != null) {
            file = mAdapter.getItem(position);
            boolean selected = setFileSelected(file);
            if (file.isDirectory()) {
                displayFolder(file);
            } else {
                if (selected) {
                    setItemSelected(position, view);
                }
            }
        } else {
            return;
        }
    }

    private static class FileAdapter extends BaseAdapter
            implements FileFilter {

        private FilePickerFilter mFilter;
        private Context mContext;
        private ArrayList<File> mFiles;
        private int mSelectedId = -1;

        public FileAdapter(Context context, File folder, FilePickerFilter filter) {
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException("not a folder: " + folder);
            }
            mContext = context;
            mFilter = filter;
            initList(folder);
        }

        private void initList(File folder) {
            File[] files = folder.listFiles(this);
            if (files == null) {
                return;
            }
            mFiles = new ArrayList<File>(files.length);
            for (File f : files) {
                mFiles.add(f);
            }
            Collections.sort(mFiles, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    // folder will always displayed in front
                    if (f1.isDirectory() && !f2.isDirectory()) {
                        return -1;
                    } else if (!f1.isDirectory() && f2.isDirectory()) {
                        return 1;
                    } else {
                        return f1.compareTo(f2);
                    }
                }
            });
        }

        public void setSelectedId(int id) {
            mSelectedId = id;
        }

        public int getSelectedid() {
            return mSelectedId;
        }

        @Override
        public int getCount() {
            return mFiles != null ? mFiles.size() : 0;
        }

        @Override
        public File getItem(int position) {
            return mFiles != null ? mFiles.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ItemView(mContext);
            }
            File file = getItem(position);
            ItemView view = (ItemView) convertView;
            view.setData(file);
            view.setId(position);
            if (position == mSelectedId) {
                view.setBackgroundColor(ITEM_BKG_COLOR_SELECTED);
            } else {
                view.setBackgroundColor(ITEM_BKG_COLOR_NORMAL);
            }
            return view;
        }

        @Override
        public boolean accept(File file) {
            if (mFilter != null) {
                return mFilter.canBeDisplayed(file);
            } else {
                // use FILTER_NORMAL_ALL by default
                // see commit on FILTER_NORMAL_ALL for details
                return FILTER_NORMAL_ALL.canBeDisplayed(file);
            }
        }
    }

    private static class ItemView extends LinearLayout {

        public ImageView mIcon;
        public TextView mName;
        // TODO: support multi-select
        public CheckBox mCheck;

        public ItemView(Context context) {
            super(context);
            this.initView();
        }

        private void initView() {
            this.setOrientation(LinearLayout.HORIZONTAL);
            this.setGravity(Gravity.CENTER_VERTICAL);
            addIcon();
            addName();
            addCheckBox();
        }

        private void addIcon() {
            Context cxt = getContext();
            int height = dip2px(cxt, ITEM_HEIGHT);
            int padding = dip2px(cxt, ITEM_ICON_PADDING);
            mIcon = new ImageView(cxt);
            mIcon.setScaleType(ScaleType.CENTER_CROP);
            mIcon.setPadding(padding, padding, padding, padding);
            LayoutParams params =
                    new LayoutParams(height, height, 0);
            this.addViewInLayout(mIcon, -1, params);
        }

        private void addName() {
            mName = new TextView(getContext());
            mName.setSingleLine();
            LayoutParams params =
                    new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
            this.addViewInLayout(mName, -1, params);
        }

        private void addCheckBox() {
            mCheck = new CheckBox(getContext());
            mCheck.setVisibility(View.GONE);
            LayoutParams params =
                    new LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0);
            this.addViewInLayout(mCheck, -1, params);
        }

        public void setData(File file) {
            if (file != null) {
                mIcon.setImageResource(file.isDirectory() ?
                        DEFAULT_FOLDER_ICON : DEFAULT_FILE_ICON);
                mName.setText(file.getName());
            }
        }
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface FilePickerCallback {

        /**
         * called when a folder is being displayed
         * @param folder
         *            the folder displayed
         */
        public void onBrowseFolder(String folder);

        /**
         * called when a file meeting the condition is being selected
         * @param file
         *            the selected file
         */
        public void onFileSelected(String file);
    }

    public interface FilePickerFilter {

        /**
         * indicating whether a specific file is what you want, AKA,
         * can be picked as a return value.
         * @param file
         *            the file to check
         * @return {@code true} if the file meets the condition, {@code false}
         *         otherwise.
         */
        public boolean canBeSelected(File file);

        /**
         * indicating whether a specific can be included in the
         * displayed list.
         * note: you may want to display most or all folders so that
         * we can browse through folders to find want you want.
         * also, a file that is not displayed can not be selected.
         * so just return true if you do not care about what is being
         * displayed.
         * @param file
         *            the file to check
         * @return {@code true} if the file meets the condition, {@code false}
         *         otherwise.
         */
        public boolean canBeDisplayed(File file);
    }

    /**
     * will display folders and files that do not begin with a dot,
     * AKA, not hidden file.
     * will accept both file and folder as return value
     */
    public static final FilePickerFilter FILTER_NORMAL_ALL = new FilePickerFilter() {

        @Override
        public boolean canBeSelected(File file) {
            return true;
        }

        @Override
        public boolean canBeDisplayed(File file) {
            if (file.getName().charAt(0) == '.') {
                return false;
            } else {
                return true;
            }
        }
    };

    /**
     * will display folders and files that do not begin with a dot,
     * AKA, not hidden file.
     * will accept file as return value
     */
    public static final FilePickerFilter FILTER_NORMAL_FILES = new FilePickerFilter() {

        @Override
        public boolean canBeSelected(File file) {
            return file.isFile();
        }

        @Override
        public boolean canBeDisplayed(File file) {
            if (file.getName().charAt(0) == '.') {
                return false;
            } else {
                return true;
            }
        }
    };
}
