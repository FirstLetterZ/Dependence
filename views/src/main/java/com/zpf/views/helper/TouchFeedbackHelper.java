package com.zpf.views.helper;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.zpf.views.type.IFeedbackView;

/**
 * @author Created by ZPF on 2021/10/21.
 */
public class TouchFeedbackHelper implements IFeedbackView, View.OnTouchListener {
    private float originalAlpha;
    private Drawable originalBackground;
    private float touchDownAlpha;
    private Drawable touchDownBackground;
    private boolean isTouchDown;

    public TouchFeedbackHelper() {
        this.touchDownAlpha = 0.8f;
        this.touchDownBackground = null;
    }

    public TouchFeedbackHelper(float touchDownAlpha, @Nullable Drawable touchDownBackground) {
        this.touchDownAlpha = touchDownAlpha;
        this.touchDownBackground = touchDownBackground;
    }

    @Override
    public void setTouchStyle(float alpha, @Nullable Drawable background) {
        this.touchDownAlpha = alpha;
        this.touchDownBackground = background;
    }

    @Override
    public void onTouch(View view) {
        if (isTouchDown) {
            return;
        }
        originalAlpha = view.getAlpha();
        originalBackground = view.getBackground();
        if (touchDownAlpha > 0f && touchDownAlpha < 1f) {
            view.setAlpha(touchDownAlpha);
        }
        if (touchDownBackground != null) {
            view.setBackground(touchDownBackground);
        }
        isTouchDown = true;
    }

    @Override
    public void onRestore(View view) {
        if (isTouchDown) {
            view.setAlpha(originalAlpha);
            view.setBackground(originalBackground);
        }
        isTouchDown = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                this.onTouch(v);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                this.onRestore(v);
                break;
        }
        return false;
    }
}