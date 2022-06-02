package com.zpf.views.helper;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewParent;

public class ViewDragCloseHelper {
    public static final int STATUS_NORMAL = 0;//正常浏览状态
    public static final int STATUS_DRAGING = 1;//滑动状态
    public static final int STATUS_RESETTING = 2;//返回中状态

    public static final float MIN_SCALE_SIZE = 0.3f;//最小缩放比例
    public static final int BACK_DURATION = 300;//ms

    private int currentStatus = STATUS_NORMAL;

    private float downX;
    private float downY;
    private float upX;
    private float upY;
    private View rootView;
    private View currentView;
    private VelocityTracker mVelocityTracker;
    private DragCloseListener closeListener;

    public void setDragCloseListener(DragCloseListener listener) {
        this.closeListener = listener;
    }

    public void setHandleView(View showView) {
        if (showView == null) {
            this.currentView = null;
            this.rootView = null;
        } else {
            this.currentView = showView;
            Context context = showView.getContext();
            if (context instanceof Activity) {
                rootView = ((Activity) context).getWindow().getDecorView();
            } else {
                ViewParent nextParent = currentView.getParent();
                while (nextParent instanceof View) {
                    rootView = (View) nextParent;
                    nextParent = nextParent.getParent();
                }
            }
        }
    }

    public boolean shouldInterceptTouchEvent(MotionEvent ev) {
        if (currentStatus == STATUS_RESETTING) {
            return true;
        }
        if (closeListener == null || currentView == null) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getRawX();
                downY = ev.getRawY();
                //边界条件
                if (closeListener.beginDragClose(currentView)) {
                    currentStatus = STATUS_DRAGING;
                    addIntoVelocity(ev);
                } else {
                    currentStatus = STATUS_NORMAL;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                return currentStatus == STATUS_DRAGING;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    public boolean handleTouchEvent(MotionEvent ev) {
        if (currentStatus == STATUS_RESETTING) {
            return true;
        }
        if (closeListener == null || currentView == null || currentStatus != STATUS_DRAGING) {
            return false;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getRawX();
                downY = ev.getRawY();
                addIntoVelocity(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                addIntoVelocity(ev);
                moveView(ev.getRawX(), ev.getRawY());
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                upX = ev.getRawX();
                upY = ev.getRawY();
                float vY = computeYVelocity();
                if (vY >= 1200 || Math.abs(upY - downY) > rootView.getHeight() * 0.25f) {
                    closeListener.onClose(currentView);
                } else {
                    resetReviewState();
                }
                break;
        }
        return true;
    }

    private ValueAnimator.AnimatorUpdateListener animatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float percent = (float) animation.getAnimatedValue();
            if (percent >= 0.99f) {
                moveView(downX, downY);
                currentStatus = STATUS_NORMAL;
            } else {
                float mX = upX + percent * (downX - upX);
                float mY = upY + percent * (downY - upY);
                moveView(mX, mY);
                currentStatus = STATUS_RESETTING;
            }
        }
    };

    //返回浏览状态
    private void resetReviewState() {
        currentStatus = STATUS_RESETTING;
        if (upX != downX || upY != downY) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
            valueAnimator.setDuration(BACK_DURATION);
            valueAnimator.addUpdateListener(animatorListener);
            valueAnimator.start();
        }
    }


    //移动View
    private void moveView(float movingX, float movingY) {
        if (currentView == null) {
            return;
        }
        float deltaX = movingX - downX;
        float deltaY = movingY - downY;
        float scale = 1f;
        float alphaPercent = 1f;
        if (deltaY > 0) {
            scale = 1 - Math.abs(deltaY) / rootView.getHeight();
            alphaPercent = 1 - Math.abs(deltaY) / (rootView.getHeight() * 0.5f);
        }
        currentView.setTranslationX(deltaX);
        currentView.setTranslationY(deltaY);
        scaleView(scale);
        rootView.setAlpha(alphaPercent);
    }

    //缩放View
    private void scaleView(float scale) {
        scale = Math.min(Math.max(scale, MIN_SCALE_SIZE), 1);
        currentView.setScaleX(scale);
        currentView.setScaleY(scale);
    }


    private void addIntoVelocity(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }


    private float computeYVelocity() {
        float result = 0;
        if (mVelocityTracker != null) {
            mVelocityTracker.computeCurrentVelocity(1000);
            result = mVelocityTracker.getYVelocity();
            releaseVelocity();
        }
        return result;
    }

    private void releaseVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public interface DragCloseListener {
        void onClose(View view);

        boolean beginDragClose(View view);
    }


}
