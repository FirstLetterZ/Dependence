package com.zpf.views.helper;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.Nullable;

public class ViewDragHelper implements View.OnTouchListener, View.OnLayoutChangeListener {
    private long downTime = 0L;
    private float downX = 0f;
    private float downY = 0f;
    private int oldMoveX = 0;
    private int oldMoveY = 0;
    private int moveX = 0;
    private int moveY = 0;
    private boolean isMoving = false;
    private volatile boolean skipLayoutChanged = true;
    private final Runnable checkLongClick = new Runnable() {
        @Override
        public void run() {
            View view = targetView;
            if (view != null) {
                view.performLongClick();
            }
        }
    };
    @Nullable
    private View targetView;

    public void setTargetView(@Nullable View view) {
        View oldView = targetView;
        targetView = view;
        moveX = 0;
        moveY = 0;
        skipLayoutChanged = false;
        if (view != null) {
            view.setOnTouchListener(this);
            view.addOnLayoutChangeListener(this);
        }
        if (oldView != null) {
            oldView.setOnTouchListener(null);
            oldView.removeOnLayoutChangeListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        View view = targetView;
        if (event == null || view == null) {
            return false;
        }
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isMoving = false;
                downX = event.getRawX();
                downY = event.getRawY();
                oldMoveX = moveX;
                oldMoveY = moveY;
                view.setPressed(true);
                downTime = System.currentTimeMillis();
                if (view.isLongClickable()) {
                    view.postDelayed(checkLongClick, ViewConfiguration.getLongPressTimeout());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int touchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
                float dX = event.getRawX() - downX;
                float dY = event.getRawY() - downY;
                if (isMoving || Math.abs(dX) > touchSlop || Math.abs(dY) > touchSlop) {
                    if (!isMoving) {
                        view.removeCallbacks(checkLongClick);
                        isMoving = true;
                    }
                    moveTo((int) (dX + oldMoveX), (int) (dY + oldMoveY));
                }
                break;
            case MotionEvent.ACTION_UP:
                long touchDuration = System.currentTimeMillis() - downTime;
                view.removeCallbacks(checkLongClick);
                view.setPressed(false);
                if (!isMoving && touchDuration <= ViewConfiguration.getTapTimeout()) {
                    view.performClick();
                }
            case MotionEvent.ACTION_CANCEL:
                isMoving = false;
                downTime = 0L;
                oldMoveX = moveX;
                oldMoveY = moveY;
                break;
        }
        return true;
    }

    public void moveBy(int dx, int dy) {
        moveTo(moveX + dx, moveY + dy);
    }

    public void moveTo(int x, int y) {
        View view = targetView;
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (!(parent instanceof View)) {
            return;
        }
        View parentView = ((View) parent);
        int parentWidth = parentView.getMeasuredWidth();
        int parentHeight = parentView.getMeasuredHeight();
        if (parentWidth <= 0 || parentHeight <= 0) {
            return;
        }
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int marginLeft = parentView.getPaddingLeft();
        int marginTop = parentView.getPaddingTop();
        int marginRight = parentView.getPaddingRight();
        int marginBottom = parentView.getPaddingBottom();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = ((ViewGroup.MarginLayoutParams) lp);
            marginLeft += mlp.leftMargin;
            marginTop += mlp.topMargin;
            marginRight += mlp.rightMargin;
            marginBottom += mlp.bottomMargin;
        }
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        int maxTransX = parentWidth - viewWidth - marginRight - marginLeft;
        int maxTransY = parentHeight - viewHeight - marginBottom - marginTop;
        int realX = Math.max(Math.min(x, maxTransX), 0);
        int realY = Math.max(Math.min(y, maxTransY), 0);
        skipLayoutChanged = true;
        view.layout(realX + marginLeft, realY + marginTop, realX + marginLeft + viewWidth, realY + marginTop + viewHeight);
        view.invalidate();
        skipLayoutChanged = false;
        moveX = realX;
        moveY = realY;
    }
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (!skipLayoutChanged) {
            skipLayoutChanged = true;
            moveTo(moveX, moveY);
        }
    }
}