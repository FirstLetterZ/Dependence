package com.zpf.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class DashedView extends View {
    private Paint mPaint;
    private int mDashColor;
    private int mDashGap;
    private int mDashWidth;
    private float mStrokeWidth;
    private Path mLinePath = new Path();

    @IntDef({DashedType.HORIZONTAL_DASHED, DashedType.VERTICAL_DASHED, DashedType.CIRCLE_DASHED})
    public @interface DashedType {
        int HORIZONTAL_DASHED = 0;
        int VERTICAL_DASHED = 1;
        int CIRCLE_DASHED = 2;
    }

    private int mDashedType = DashedType.HORIZONTAL_DASHED;

    public DashedView(Context context) {
        super(context);
        initTypedArray(null);
    }

    public DashedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(context.obtainStyledAttributes(attrs, R.styleable.DashedView));
    }

    public DashedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypedArray(context.obtainStyledAttributes(attrs, R.styleable.DashedView));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DashedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initTypedArray(context.obtainStyledAttributes(attrs, R.styleable.DashedView));
    }

    private void initTypedArray(@Nullable TypedArray typedArray) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        float d = getContext().getResources().getDisplayMetrics().density;
        if (typedArray != null) {
            mDashColor = typedArray.getColor(R.styleable.DashedView_dashColor, Color.TRANSPARENT);
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.DashedView_strokeWidth, 0);
            mDashGap = typedArray.getDimensionPixelSize(R.styleable.DashedView_dashGap, 0);
            mDashWidth = typedArray.getDimensionPixelSize(R.styleable.DashedView_dashWidth, 1);
            mDashedType = typedArray.getInt(R.styleable.DashedView_dashType, DashedType.HORIZONTAL_DASHED);
            typedArray.recycle();
        } else {
            mDashColor = Color.DKGRAY;
        }
        if (mStrokeWidth <= 0) {
            mStrokeWidth = d;
        }
        if (mDashWidth <= 0) {
            mDashWidth = (int) (2 * d);
        }
        if (mDashGap <= 0) {
            mDashGap = (int) (2 * d);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPaint.setPathEffect(new DashPathEffect(new float[]{mDashWidth, mDashGap}, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mDashColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        if (mDashedType == DashedType.HORIZONTAL_DASHED) {
            mLinePath.reset();
            mLinePath.moveTo(0, getHeight() * 0.5f);
            mLinePath.lineTo(getWidth(), getHeight() * 0.5f);
            canvas.drawPath(mLinePath, mPaint);
        } else if (mDashedType == DashedType.VERTICAL_DASHED) {
            mLinePath.reset();
            mLinePath.moveTo(getWidth() * 0.5f, 0);
            mLinePath.lineTo(getWidth() * 0.5f, getHeight());
            canvas.drawPath(mLinePath, mPaint);
        } else if (mDashedType == DashedType.CIRCLE_DASHED) {
            float radius = Math.min(getWidth(), getHeight()) * 0.5f - mStrokeWidth * 0.5f;
            canvas.drawCircle(getWidth() * 0.5f, getHeight() * 0.5f, radius, mPaint);
        }
    }

    public void setDashColor(int color) {
        mDashColor = color;
    }

    public void setStrokeWidth(float width) {
        if (width > 0 && this.mStrokeWidth != width) {
            this.mStrokeWidth = width;
        }
    }

    public void setGapAndWidth(int gap, int width) {
        if (width > 0 && width != mDashWidth) {
            mDashWidth = width;
        }
        if (gap > 0 && mDashGap != gap) {
            mDashGap = gap;
        }
    }
}
