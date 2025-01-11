package com.zpf.tool.animation;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ViewAnimAttribute extends AnimAttribute {
    public final View moveView;
    public View targetView;
    public ViewAnimAttribute(@NonNull View moveView) {
        this(moveView, null);
    }
    public ViewAnimAttribute(@NonNull View moveView, @Nullable View targetView) {
        this.moveView = moveView;
        this.targetView = targetView;
    }

    public void setTargetView(@Nullable View targetView) {
        this.targetView = targetView;
    }

    public void run(float percent) {
        float realPercent = Math.max(Math.min(percent, 1f), 0f);
        if (moveView.getVisibility() == View.GONE) {
            return;
        }
        this.run(moveView, realPercent);
    }

    public void initAttribute() {
        scaleX1 = moveView.getScaleX();
        scaleY1 = moveView.getScaleY();
        rotationX1 = moveView.getRotationX();
        rotationY1 = moveView.getRotationY();
        rotationZ1 = moveView.getRotation();
        x1 = moveView.getTranslationX();
        y1 = moveView.getTranslationY();
        if (targetView != null) {
            scaleX2 = targetView.getMeasuredWidth() * 1f / moveView.getMeasuredWidth();
            scaleY2 = targetView.getMeasuredHeight() * 1f / moveView.getMeasuredHeight();
            x2 = targetView.getX() - moveView.getLeft() + (targetView.getMeasuredWidth() - moveView.getMeasuredWidth()) / 2f;
            y2 = targetView.getY() - moveView.getTop() + (targetView.getMeasuredHeight() - moveView.getMeasuredHeight()) / 2f;
        }
    }

}