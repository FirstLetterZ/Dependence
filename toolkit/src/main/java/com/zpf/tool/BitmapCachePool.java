package com.zpf.tool;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class BitmapCachePool {
    private Bitmap frontBitmap = null;
    private Bitmap backBitmap = null;
    private final AtomicBoolean locking = new AtomicBoolean(false);
    private final int[] byteArray;
    private final int width;
    private final int height;

    public BitmapCachePool(int width, int height) {
        this.width = width;
        this.height = height;
        byteArray = new int[width * height];
    }

    public boolean checkSize(int targetWidth, int targetHeight) {
        return targetWidth == width && targetHeight == height;
    }

    @Nullable
    public int[] lock() {
        if (locking.get()) {
            return null;
        }
        locking.set(true);
        return byteArray;
    }

    @Nullable
    public Bitmap unlock() {
        if (!locking.get()) {
            return null;
        }
        Bitmap bitmap = obtainBitmap();
        bitmap.setPixels(byteArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        Bitmap temp = frontBitmap;
        frontBitmap = bitmap;
        backBitmap = temp;
        return bitmap;
    }

    @Nullable
    public Bitmap read() {
        return frontBitmap;
    }

    public void release() {
        locking.set(false);
        frontBitmap = null;
        backBitmap = null;
    }

    @NonNull
    private Bitmap obtainBitmap() {
        Bitmap cacheBitmap = backBitmap;
        backBitmap = null;
        if (cacheBitmap == null || cacheBitmap.isRecycled() || cacheBitmap.getWidth() != width || cacheBitmap.getHeight() != height) {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            return cacheBitmap;
        }
    }
}