package com.lx.mystalecode.activity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lx.mystalecode.R;
import com.lx.mystalecode.utils.AlertUtils;
import com.lx.mystalecode.utils.FileUtils;
import com.lx.mystalecode.view.FilePicker.FilePickerDialog;
import com.lx.mystalecode.view.FilePicker.FilePickerList;

import java.io.File;

final public class ActivityFilePickerDemo extends BaseActivity implements
        BaseActivity.SwipeBackImpl,
        View.OnClickListener {

    TextView mTvFolder;
    TextView mTvFile;
    FilePickerList mFilePicker;
    Button mBtnBack;
    Button mBtnDlg1;
    Button mBtnDlg2;
    Button mBtnDlg3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker_demo);

        mTvFolder = (TextView) findViewById(R.id.afpd_tv_cur_dir);
        mTvFile = (TextView) findViewById(R.id.afpd_tv_selected_file);
        mFilePicker = (FilePickerList) findViewById(R.id.afpd_filepicker);

        mBtnBack = (Button) findViewById(R.id.afpd_btn_back);
        mBtnDlg1 = (Button) findViewById(R.id.afpd_btn_start_dlg1);
        mBtnDlg2 = (Button) findViewById(R.id.afpd_btn_start_dlg2);
        mBtnDlg3 = (Button) findViewById(R.id.afpd_btn_start_dlg3);
        mBtnDlg1.setOnClickListener(this);
        mBtnDlg2.setOnClickListener(this);
        mBtnDlg3.setOnClickListener(this);

        initFilePicker();
    }

    // a filter is not set, the default filter will be used
    private void initFilePicker() {
        mFilePicker.setFilePickerCallback(new FilePickerList.FilePickerCallback() {
            @Override
            public void onBrowseFolder(String folder) {
                // display current folder
                mTvFolder.setText(folder);
            }

            @Override
            public void onFileSelected(String file) {
                // display selected file
                mTvFile.setText(file);
            }
        });

        // handle browse back
        mBtnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilePicker.backToParentFolder();
            }
        });

        // start to browse
        mFilePicker.refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.afpd_btn_start_dlg1:
            startDlg1();
            break;
        case R.id.afpd_btn_start_dlg2:
            startDlg2();
            break;
        case R.id.afpd_btn_start_dlg3:
            startDlg3();
            break;
        }
    }

    // find files with filter FILTER_NORMAL_FILES
    private void startDlg1() {
        new FilePickerDialog.Builder(this)
                .setFilePickerFilter(FilePickerList.FILTER_NORMAL_FILES)
                .setFileSelectCallback(new FilePickerDialog.FileSelectCallback() {
                    @Override
                    public void onFileSelected(String file) {
                        Toast.makeText(ActivityFilePickerDemo.this, file,
                                Toast.LENGTH_LONG).show();
                    }
                }).create().show();
    }

    // find jpg files in DCIM folder
    private void startDlg2() {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).toString();
        new FilePickerDialog.Builder(this)
                .setRoot(root)
                .setFilePickerFilter(new FilePickerList.FilePickerFilter() {

                    @Override
                    public boolean canBeSelected(File file) {
                        // accept files with suffix "jpg" as result
                        if (file.isDirectory()) {
                            return false;
                        }
                        return file.getName().endsWith(".jpg");
                    }

                    @Override
                    public boolean canBeDisplayed(File file) {
                        // display everything except for hidden files
                        // some file is displayed, but can not be selected,
                        // like *.mp4 files.
                        if (file.getName().charAt(0) == '.') {
                            return false;
                        } else {
                            return true;
                        }
                    }

                })
                .setFileSelectCallback(new FilePickerDialog.FileSelectCallback() {
                    @Override
                    public void onFileSelected(String file) {
                        Toast.makeText(ActivityFilePickerDemo.this, file,
                                Toast.LENGTH_LONG).show();
                    }
                }).create().show();
    }

    // demo for MediaScannerUtils
    // use MediaScanner to insert entries of the selected
    // file or dir into database
    private void startDlg3() {
        final FileUtils.ScannerListener listener = new FileUtils.ScannerListener() {
            @Override
            public void onScanCompleted(String path, boolean success) {
                Log.d("liuxu", "scan complete, success: " + success);
                Toast.makeText(ActivityFilePickerDemo.this,
                        "scan file " + (success ? "success: " : "failed: ") + path,
                        Toast.LENGTH_LONG).show();
            }
        };

        new FilePickerDialog.Builder(this)
                .setFilePickerFilter(new FilePickerList.FilePickerFilter() {

                    @Override
                    public boolean canBeSelected(File file) {
                        // everything can be scanned
                        return true;
                    }

                    @Override
                    public boolean canBeDisplayed(File file) {
                        // display everything except for hidden files
                        if (file.getName().charAt(0) == '.') {
                            return false;
                        } else {
                            return true;
                        }
                    }

                })
                .setFileSelectCallback(new FilePickerDialog.FileSelectCallback() {
                    @Override
                    public void onFileSelected(final String file) {
                        if (file.equals(Environment.getExternalStorageDirectory())) {
                            AlertUtils.showToastShort("should not scan sd root");
                            return;
                        }
                        Log.d("liuxu", "about to scan file: " + file);
                        FileUtils.scanFile(
                                ActivityFilePickerDemo.this,
                                file, listener);
                    }
                }).create().show();
    }
}
