package com.zpf.tool.animation;

import android.view.View;

import androidx.annotation.NonNull;

public class ViewAnimAttribute extends AnimAttribute {

    protected final boolean initBeforeStart;
    protected final View view;
    public ViewAnimAttribute(@NonNull View view) {
        this(view, false);
    }
    public ViewAnimAttribute(@NonNull View view, boolean initBeforeStart) {
        this.view = view;
        this.initBeforeStart = initBeforeStart;
    }

    public void run(float percent) {
        if (percent <= 0f && initBeforeStart) {
            initAttribute();
        }
        if (view.getVisibility() == View.GONE) {
            return;
        }
        this.run(view, percent);
    }

    protected void initAttribute() {
        scaleX1 = view.getScaleX();
        scaleY1 = view.getScaleY();
        rotationX1 = view.getRotationX();
        rotationY1 = view.getRotationY();
        rotationZ1 = view.getRotation();
        x1 = view.getX();
        y1 = view.getY();
    }

}