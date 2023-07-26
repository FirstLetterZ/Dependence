package com.zpf.apptest.tst;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.zpf.views.ViewDrawingCanvas;
import com.zpf.views.helper.ViewRoundHelper;

public class RoundLayout extends LinearLayout {
    private final ViewRoundHelper roundHelper = new ViewRoundHelper();
    private boolean skipDraw = true;

    public RoundLayout(Context context) {
        super(context);
    }

    public RoundLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDrawCircle(boolean circle) {
        roundHelper.setDrawCircle(circle);
    }

    public void setConnerRadius(float radius) {
        roundHelper.setConnerRadius(radius);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        roundHelper.prepareCanvas(w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        skipDraw = false;
        ViewDrawingCanvas viewDrawingCanvas = roundHelper.getDrawingCanvas();
        if (viewDrawingCanvas == null) {
            super.draw(canvas);
        } else {
            super.draw(viewDrawingCanvas.canvas);
            roundHelper.ModifyCanvas(canvas);
        }
        skipDraw = true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
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
    }
}
