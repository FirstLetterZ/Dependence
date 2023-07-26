package com.zpf.views;

import android.graphics.Canvas;

import androidx.annotation.NonNull;

public interface IDrawingCanvasModifier {
    void prepareCanvas(int width, int height);

    boolean ModifyCanvas(@NonNull Canvas canvas);
}
