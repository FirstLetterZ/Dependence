package com.zpf.tool;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class RelativeButtonLayout extends RelativeLayout implements IButtonLayout {
    private float touchAlpha = 0.8f;

    public RelativeButtonLayout(Context context) {
        super(context);
    }

    public RelativeButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeButtonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                onTouchDown();
            }
            case MotionEvent.ACTION_UP: {
                onTouchUp();
            }
            case MotionEvent.ACTION_CANCEL:
                onTouchUp();
            case MotionEvent.ACTION_OUTSIDE:
                onTouchUp();
        }
        return super.dispatchTouchEvent(ev);
    }

    public float getTouchAlpha() {
        return touchAlpha;
    }

    public void setTouchAlpha(float touchAlpha) {
        this.touchAlpha = touchAlpha;
    }

    @Override
    public void onTouchDown() {
        if (isEnabled()) {
            setAlpha(touchAlpha);
        }
    }

    @Override
    public void onTouchUp() {
        setAlpha(1.0f);
    }
}