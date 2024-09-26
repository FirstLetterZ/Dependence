package com.zpf.tool;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by ZPF on 2018/11/21.
 */
public class OnTouchClickListener implements View.OnTouchListener {
    private final View.OnClickListener clickListener;
    private long downTime;
    private float downX, downY;
    private boolean callClick = false;
    private int touchSlopSquare;

    public OnTouchClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (touchSlopSquare == 0) {
                    ViewConfiguration configuration = ViewConfiguration.get(v.getContext());
                    int touchSlop = configuration.getScaledTouchSlop();
                    touchSlopSquare = touchSlop * touchSlop;
                }
                callClick = true;
                downTime = System.currentTimeMillis();
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = downX - event.getX();
                float dy = downY - event.getY();
                if (dx * dx + dy * dy > touchSlopSquare) {
                    callClick = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                long deltaTime = System.currentTimeMillis() - downTime;
                if (callClick && deltaTime < 800) {
                    if (clickListener != null) {
                        clickListener.onClick(v);
                    }
                }
                break;
            default:
                callClick = false;
                break;
        }
        return false;
    }
}
