package com.zpf.tool.animation;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AnimAttribute {
    protected float x1 = 0f;
    protected float x2 = 0f;
    protected float y1 = 0f;
    protected float y2 = 0f;
    protected float scaleX1 = 1f;
    protected float scaleX2 = 1f;
    protected float scaleY1 = 1f;
    protected float scaleY2 = 1f;
    protected float rotationX1 = 0f;
    protected float rotationX2 = 0f;
    protected float rotationY1 = 0f;
    protected float rotationY2 = 0f;
    protected float rotationZ1 = 0f;
    protected float rotationZ2 = 0f;
    private float lastPercent = 1f;

    public void run(@Nullable View view, float percent) {
        if (view == null) {
            return;
        }
        if (scaleX1 != scaleX2) {
            view.setScaleX(scaleX1 + percent * (scaleX2 - scaleX1));
        } else {
            view.setScaleX(scaleX2);
        }
        if (scaleY1 != scaleY2) {
            view.setScaleY(scaleY1 + percent * (scaleY2 - scaleY1));
        } else {
            view.setScaleY(scaleY2);
        }
        if (rotationX1 != rotationX2) {
            view.setRotationX(rotationX1 + percent * (rotationX2 - rotationX1));
        } else {
            view.setRotationX(rotationX2);
        }
        if (rotationY1 != rotationY2) {
            view.setRotationY(rotationY1 + percent * (rotationY2 - rotationY1));
        } else {
            view.setRotationY(rotationY2);
        }
        if (rotationZ1 != rotationZ2) {
            view.setRotation(rotationZ1 + percent * (rotationZ2 - rotationZ1));
        } else {
            view.setRotation(rotationZ2);
        }
        if (x2 != x1 || y2 != y1) {
            handleTranslate(view, x1 + percent * (x2 - x1), y1 + percent * (y2 - y1));
        } else if (percent < lastPercent) {
            handleTranslate(view, x2, y2);
        }
        lastPercent = percent;
    }

    protected void handleTranslate(@NonNull View view, float x, float y) {
        view.setTranslationX(x);
        view.setTranslationY(y);
    }

    public void reset() {
        x1 = 0f;
        x2 = 0f;
        y1 = 0f;
        y2 = 0f;
        scaleX1 = 1f;
        scaleX2 = 1f;
        scaleY1 = 1f;
        scaleY2 = 1f;
        rotationX1 = 0f;
        rotationX2 = 0f;
        rotationY1 = 0f;
        rotationY2 = 0f;
        rotationZ1 = 0f;
        rotationZ2 = 0f;
        lastPercent = 1f;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" +
                "x1=" + x1 +
                ", x2=" + x2 +
                ", y1=" + y1 +
                ", y2=" + y2 +
                ", scaleX1=" + scaleX1 +
                ", scaleX2=" + scaleX2 +
                ", scaleY1=" + scaleY1 +
                ", scaleY2=" + scaleY2 +
                ", rotationX1=" + rotationX1 +
                ", rotationX2=" + rotationX2 +
                ", rotationY1=" + rotationY1 +
                ", rotationY2=" + rotationY2 +
                ", rotationZ1=" + rotationZ1 +
                ", rotationZ2=" + rotationZ2 +
                ", lastPercent=" + lastPercent +
                '}';
    }
}
