package com.zpf.aaa;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.zpf.api.IResultBean;
import com.zpf.api.OnDataResultListener;
import com.zpf.api.OnProgressListener;
import com.zpf.support.network.util.OkHttpNetUtil;
import com.zpf.update.FileVersionInfo;
import com.zpf.update.IAlertUpdateCallback;
import com.zpf.update.IDownloadListener;
import com.zpf.update.INetCall;
import com.zpf.update.INetResultListener;
import com.zpf.update.IUpdateListener;
import com.zpf.update.UpdateManager;

import java.io.File;
import java.io.IOException;

/**
 * @author Created by ZPF on 2021/3/30.
 */
public class MainActivity extends ComponentActivity {
    private final String versionUrl = "http://apk.weimob.com/tes/queryVersion";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tvMag = findViewById(R.id.tv_msg);
        final UpdateManager updateManager = new UpdateManager(this);
        updateManager.setAutoUpZip(false);
        updateManager.setValidTime(1);
        updateManager.setNetCall(new INetCall() {
            @Override
            public void checkVersion(final FileVersionInfo versionInfo, final INetResultListener listener) {
                OkHttpNetUtil.requestGetCall(versionUrl, null, new OnDataResultListener<IResultBean<String>>() {
                    @Override
                    public void onResult(boolean success, @Nullable IResultBean<String> data) {
                        if (success && data != null && data.isSuccess()) {
                            FileVersionInfo fileVersionInfo = new FileVersionInfo(
                                    versionInfo.getFileName(), data.getData());
                            tvMag.setText("获得新版本信息：" + fileVersionInfo.toString());
                            listener.onSuccess(fileVersionInfo);
                        } else if (data != null) {
                            listener.onFail(versionInfo.getFileName(), data.getCode(), data.getMessage());
                        } else {
                            listener.onFail(versionInfo.getFileName(), -99, getResources().getString(R.string.network_data_null));
                        }
                    }
                }, null);
            }

            @Override
            public void download(final FileVersionInfo fileVersionInfo, final IDownloadListener listener) {
                File target = new File(fileVersionInfo.getLocalPath());
                if (!target.exists()) {
                    try {
                        target.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                OkHttpNetUtil.download(fileVersionInfo.getDownloadPath(), target, null, 0, new OnDataResultListener<IResultBean<String>>() {
                    @Override
                    public void onResult(boolean success, @Nullable IResultBean<String> data) {
                        if (success && data != null && data.isSuccess()) {
                            tvMag.setText("下载成功...");
                            listener.onSuccess(fileVersionInfo);
                        } else if (data != null) {
                            listener.onFail(fileVersionInfo.getFileName(), data.getCode(), data.getMessage());
                        } else {
                            listener.onFail(fileVersionInfo.getFileName(), -99, getResources().getString(R.string.network_data_null));
                        }
                    }
                }, new OnProgressListener() {
                    @Override
                    public void onChanged(long total, long current) {
                        listener.onChanged(fileVersionInfo.getFileName(), total, current);
                        Log.e("ZPF", "total=" + total + ";current=" + current);
                    }
                }, null);
            }
        });


        final ProgressBar pbBar = findViewById(R.id.pb_bar);
        pbBar.setMax(1000);
        final ProgressBar pbLoading = findViewById(R.id.pb_loading);
        final Button btnCheck = findViewById(R.id.btn_check);
        final IUpdateListener listener = new IUpdateListener() {
            @Override
            public void onStart(String fileName) {
                pbBar.setProgress(0);
                pbLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(FileVersionInfo versionInfo) {
                pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onChanged(String fileName, long total, long current) {
                pbBar.setProgress((int) (total * 1.0f / current * 1000));
            }

            @Override
            public void onFail(String fileName, int code, String message) {
                pbLoading.setVisibility(View.GONE);
                tvMag.setText(("" + code + "-" + message));

            }

            @Override
            public boolean alertUpdate(FileVersionInfo versionInfo, IAlertUpdateCallback callback) {
                return false;
            }
        };
        updateManager.setLoadListener("live.app", listener);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateManager.checkFileVersion("live.app");
            }
        });
    }


}
