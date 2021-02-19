package com.zpf.apptest.down;

import android.app.Application;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

/**
 * @author Created by ZPF on 2021/2/19.
 */
public class DownloadUtil {
    private final DownloadManager downloadManager;
    private final Application application;
    private long downloadId;
    private final int[] loadState = new int[3];
    private BroadcastReceiver receiver;
//    private OnProgressListener

    public DownloadUtil(Context context) {
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        application = (Application) context.getApplicationContext();
    }

    public void download(String url, String title, String desc, String apkName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription(desc);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setAllowedOverRoaming(false);
        request.setDestinationInExternalFilesDir(application, Environment.DIRECTORY_DOWNLOADS, apkName);
        loadState[0] = 0;
        loadState[1] = Integer.MAX_VALUE;
        loadState[2] = DownloadManager.STATUS_PENDING;
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    application.unregisterReceiver(receiver);
                }
            };
        }
        application.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadId = downloadManager.enqueue(request);
//        downloadManager.getUriForDownloadedFile(downloadId);
    }

    private void queryState() {
        Cursor c = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
        if (c != null && c.moveToFirst()) {
            loadState[0] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            loadState[1] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            loadState[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
    }

    private void onStateChanged() {
        int current = loadState[0];
        int total = loadState[1];
        int status = loadState[2];
        switch (status) {
            case DownloadManager.STATUS_SUCCESSFUL: {
                Uri apkUri = downloadManager.getUriForDownloadedFile(downloadId);
            }
            case DownloadManager.STATUS_FAILED: {

            }
            default: {

            }
        }
    }

}
