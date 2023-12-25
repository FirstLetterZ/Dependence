package com.zpf.views.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.zpf.views.helper.ViewDrawingCanvas;
import com.zpf.views.helper.ViewRoundHelper;
import com.zpf.views.type.IFeedbackView;
import com.zpf.views.type.IRoundView;

public class RelativeButtonLayout extends RelativeLayout implements IFeedbackView, IRoundView {
    private final TouchFeedbackDelegate delegate = new TouchFeedbackDelegate(0.8f, null);
    private ViewRoundHelper roundHelper = null;
    private boolean skipDraw = true;

    public RelativeButtonLayout(Context context) {
        super(context);
    }

    public RelativeButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeButtonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RelativeButtonLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isEnabled() && (isClickable() || isLongClickable())) {
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

    public void setDrawCircle(boolean circle) {
        prepareViewRoundHelper().setDrawCircle(circle);
    }

    @Override
    public boolean isDrawCircle() {
        if (roundHelper != null) {
            return roundHelper.isDrawCircle();
        }
        return false;
    }

    public void setConnerRadius(float radius) {
        prepareViewRoundHelper().setConnerRadius(radius);
    }
    @Override
    public float getConnerRadius() {
        if (roundHelper != null) {
            return roundHelper.getConnerRadius();
        }
        return 0f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (roundHelper != null) {
            roundHelper.prepareCanvas(w, h);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (roundHelper != null && roundHelper.isEnable()) {
            skipDraw = false;
            ViewDrawingCanvas viewDrawingCanvas = roundHelper.getDrawingCanvas();
            if (viewDrawingCanvas == null) {
                super.draw(canvas);
            } else {
                super.draw(viewDrawingCanvas.canvas);
                roundHelper.ModifyCanvas(canvas);
            }
            skipDraw = true;
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (roundHelper != null && roundHelper.isEnable()) {
            if (skipDraw) {
                ViewDrawingCanvas viewDrawingCanvas = roundHelper.getDrawingCanvas();
                if (viewDrawingCanvas == null) {
                    super.dispatchDraw(canvas);
                } else {
                    super.dispatchDraw(viewDrawingCanvas.canvas);
                    roundHelper.ModifyCanvas(canvas);
                }
            } else {
                super.dispatchDraw(canvas);
            }
        } else {
            super.dispatchDraw(canvas);
        }
    }

    protected ViewRoundHelper prepareViewRoundHelper() {
        ViewRoundHelper oldHelper = roundHelper;
        if (oldHelper != null) {
            return oldHelper;
        }
        ViewRoundHelper newHelper = new ViewRoundHelper();
        roundHelper = newHelper;
        return newHelper;

    }
}