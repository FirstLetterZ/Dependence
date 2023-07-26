package com.zpf.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ViewDrawingCanvas {
    public final Bitmap bitmap;
    public final Canvas canvas;

    public ViewDrawingCanvas(int width, int height) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

}
