package com.zpf.tool;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScreenShotHelper {
    private static final String[] KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap", "snap", "截屏"
    };

    /**
     * 读取媒体数据库时需要读取的列
     */
    private static final String[] MEDIA_PROJECTIONS = {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_ADDED
    };
    /**
     * 内部存储器内容观察者
     */
    private final ContentObserver mInternalObserver = new MediaContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, null);
    /**
     * 外部存储器内容观察者
     */
    private final ContentObserver mExternalObserver = new MediaContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null);
    private final ContentResolver mResolver;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private OnScreenShotListener listener;
    private String lastData;
    private final Runnable shotCallBack = new Runnable() {
        @Override
        public void run() {
            if (listener != null) {
                final String path = lastData;
                if (path != null && path.length() > 0) {
                    listener.onShot(path);
                }
            }
        }
    };

    public ScreenShotHelper(@NonNull Context context) {
        mResolver = context.getApplicationContext().getContentResolver();
    }

    public void setScreenShotListener(@Nullable OnScreenShotListener listener) {
        this.listener = listener;
    }

    public boolean isRunning() {
        return running.get();
    }

    @RequiresPermission(anyOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES})
    public void start() {
        if (running.get()) {
            return;
        }
        running.set(true);
        mResolver.registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, true, mInternalObserver);
        mResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mExternalObserver);
    }

    public void stop() {
        if (!running.get()) {
            return;
        }
        running.set(false);
        mResolver.unregisterContentObserver(mInternalObserver);
        mResolver.unregisterContentObserver(mExternalObserver);
    }

    private void handleMediaContentChange(Uri contentUri) {
        Cursor cursor = null;
        try {
            // 数据改变时查询数据库中最后加入的一条数据
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Bundle queryArgs = new Bundle();
                queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);
                queryArgs.putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, new String[]{MediaStore.Images.ImageColumns.DATE_ADDED});
                queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, 0);
                queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, 1);
                cursor = mResolver.query(contentUri, MEDIA_PROJECTIONS, queryArgs, null);
            } else {
                cursor = mResolver.query(contentUri, MEDIA_PROJECTIONS, null, null, MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1");
            }
            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {
                return;
            }
            // 获取各列的索引
            int dataIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            int dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
            int dateAddIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED);
            // 获取行数据
            final String data = cursor.getString(dataIndex);
            long dateTaken = cursor.getLong(dateTakenIndex);
            long dateAdded = cursor.getLong(dateAddIndex);
            if (data.length() > 0) {
                if (TextUtils.equals(lastData, data)) {
                    //更改资源文件名也会触发，并且传递过来的是之前的截屏文件，所以只对2分钟以内的有效
                    if (System.currentTimeMillis() - dateTaken < 7200) {
                        handler.removeCallbacks(shotCallBack);
                        handler.postDelayed(shotCallBack, 500);
                    }
                } else if (dateTaken == 0 || dateTaken == dateAdded * 1000) {
                    //此时为缩略图
                    handler.removeCallbacks(shotCallBack);
                    if (listener != null) {
                        listener.onShot(null);
                    }
                } else if (checkScreenShot(data)) {
                    handler.removeCallbacks(shotCallBack);
                    lastData = data;
                    handler.postDelayed(shotCallBack, 500);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * 根据包含关键字判断是否是截屏
     */
    private boolean checkScreenShot(String data) {
        if (data == null || data.length() < 2) {
            return false;
        }
        data = data.toLowerCase();
        for (String keyWork : KEYWORDS) {
            if (data.contains(keyWork)) {
                return true;
            }
        }
        return false;
    }

    private class MediaContentObserver extends ContentObserver {
        private final Uri mContentUri;

        MediaContentObserver(Uri contentUri, Handler handler) {
            super(handler);
            mContentUri = contentUri;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (listener != null) {
                handleMediaContentChange(mContentUri);
            }
        }
    }

    public interface OnScreenShotListener {
        void onShot(@Nullable String data);
    }

}