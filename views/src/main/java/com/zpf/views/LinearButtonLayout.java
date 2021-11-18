package com.zpf.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.zpf.views.type.IFeedbackView;

public class LinearButtonLayout extends LinearLayout implements IFeedbackView {
    private final TouchFeedbackDelegate delegate = new TouchFeedbackDelegate(0.8f, null);

    public LinearButtonLayout(Context context) {
        super(context);
    }

    public LinearButtonLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearButtonLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinearButtonLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                onTouch(this);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                onRestore(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setTouchStyle(float alpha, Drawable background) {
        delegate.setTouchStyle(alpha, background);
    }

    @Override
    public void onTouch(View view) {
        delegate.onTouch(view);
    }

    @Override
    public void onRestore(View view) {
        delegate.onRestore(view);
    }
}