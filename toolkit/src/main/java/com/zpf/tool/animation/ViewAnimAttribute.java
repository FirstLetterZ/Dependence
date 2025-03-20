package com.zpf.tool.animation;

import android.view.View;
import android.view.ViewGroup;

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
            int startWidth = getViewWidth(moveView);
            int startHeight = getViewHeight(moveView);
            int endWidth = getViewWidth(targetView);
            int endHeight = getViewHeight(targetView);
            if (startWidth > 0) {
                scaleX2 = endWidth * 1f / startWidth;
            } else {
                scaleX2 = scaleX1;
            }
            if (startHeight > 0) {
                scaleY2 = endHeight * 1f / startHeight;
            } else {
                scaleY2 = scaleY1;
            }
            x2 = targetView.getX() + endWidth / 2f - (moveView.getX() + startWidth / 2f) + x1;
            y2 = targetView.getY() + endHeight / 2f - (moveView.getY() + startHeight / 2f) + y1;
        } else {
            x2 = x1;
            y2 = y1;
        }
    }

    private int getViewWidth(View view) {
        int width = view.getWidth();
        if (width <= 0) {
            width = view.getMeasuredWidth();
        }
        if (width <= 0) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                width = lp.width;
            }
        }
        return width;
    }

    private int getViewHeight(View view) {
        int height = view.getHeight();
        if (height <= 0) {
            height = view.getMeasuredHeight();
        }
        if (height <= 0) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp != null) {
                height = lp.height;
            }
        }
        return height;
    }
}