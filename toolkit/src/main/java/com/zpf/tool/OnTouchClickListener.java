package com.zpf.tool;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ZPF on 2018/11/21.
 */
public class OnTouchClickListener implements View.OnTouchListener {
    private long lastDownTime;
    private float lastY, lastX;
    private View.OnClickListener clickListener;
    private final float shakeFilter;

    public OnTouchClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
        shakeFilter = 5 * Resources.getSystem().getDisplayMetrics().density;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = event.getRawX();
            lastY = event.getRawY();
            lastDownTime = System.currentTimeMillis();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if ((System.currentTimeMillis() - lastDownTime < 800)
                    && (Math.abs(event.getRawX() - lastX) < shakeFilter)
                    && (Math.abs(event.getRawY() - lastY) < shakeFilter)) {
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
            }
        } else if (event.getAction() != MotionEvent.ACTION_MOVE) {
            lastDownTime = 0;
        }
        return false;
    }
}
