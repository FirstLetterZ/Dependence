package com.zpf.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class DashedView extends View {
    private Paint drawPaint;
    private int dashedType = DashedType.HORIZONTAL_DASHED;
    private int dashedColor;
    private int dashedGap;
    private int dashedWidth;
    private float dashedRadius;
    private float strokeWidth;

    @IntDef({DashedType.HORIZONTAL_DASHED, DashedType.VERTICAL_DASHED, DashedType.CIRCLE_DASHED})
    public @interface DashedType {
        int HORIZONTAL_DASHED = 0;
        int VERTICAL_DASHED = 1;
        int CIRCLE_DASHED = 2;
        int RECT_DASHED = 3;
    }

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
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        if (typedArray != null) {
            dashedType = typedArray.getInt(R.styleable.DashedView_dashedType, DashedType.HORIZONTAL_DASHED);
            dashedGap = typedArray.getDimensionPixelSize(R.styleable.DashedView_dashedGap, 0);
            dashedWidth = typedArray.getDimensionPixelSize(R.styleable.DashedView_dashedWidth, 0);
            dashedColor = typedArray.getColor(R.styleable.DashedView_dashedColor, Color.TRANSPARENT);
            dashedRadius = typedArray.getDimensionPixelSize(R.styleable.DashedView_dashedRadius, -1);
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.DashedView_strokeWidth, 0);
            typedArray.recycle();
        }
        if (strokeWidth > 0) {
            drawPaint.setStrokeWidth(strokeWidth);
        }
        if (dashedGap > 0 && dashedWidth > 0) {
            drawPaint.setPathEffect(new DashPathEffect(new float[]{dashedWidth, dashedGap}, 0));
        }
        if (dashedColor != 0) {
            drawPaint.setColor(dashedColor);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        if (height <= 0 || width <= 0 || strokeWidth <= 0 || dashedColor == 0 || dashedWidth <= 0 || dashedGap <= 0) {
            return;
        }
        if (dashedType == DashedType.HORIZONTAL_DASHED) {
            canvas.drawLine(0, height * 0.5f, width, height * 0.5f, drawPaint);
        } else if (dashedType == DashedType.VERTICAL_DASHED) {
            canvas.drawLine(getWidth() * 0.5f, 0, getWidth() * 0.5f, getHeight(), drawPaint);
        } else if (dashedType == DashedType.CIRCLE_DASHED) {
            float radius = dashedRadius;
            float maxRadius = Math.min(width, height) * 0.5f - strokeWidth * 0.5f;
            if (radius > 0f) {
                radius = Math.min(radius, maxRadius);
            } else {
                radius = maxRadius;
            }
            canvas.drawCircle(width * 0.5f, height * 0.5f, radius, drawPaint);
        } else if (dashedType == DashedType.RECT_DASHED) {
            float radius = dashedRadius;
            float delta = strokeWidth * 0.5f;
            if (radius > 0f) {
                float maxRadius = Math.min(width, height) * 0.5f - delta;
                radius = Math.min(radius, maxRadius);
                canvas.drawRoundRect(delta, delta, width - delta, height - delta, radius, radius, drawPaint);
            } else {
                canvas.drawRect(delta, delta, width - delta, height - delta, drawPaint);
            }
        }
    }

    public void setDashedRadius(float radius) {
        if (dashedRadius != radius) {
            dashedRadius = radius;
            if (dashedType == DashedType.CIRCLE_DASHED || dashedType == DashedType.RECT_DASHED) {
                postInvalidate();
            }
        }
    }

    public void setDashedType(@IntRange(from = DashedType.HORIZONTAL_DASHED, to = DashedType.RECT_DASHED) int type) {
        if (dashedType != type) {
            dashedType = type;
            postInvalidate();
        }
    }

    public void setDashedColor(int color) {
        if (dashedColor != color) {
            dashedColor = color;
            drawPaint.setColor(color);
            postInvalidate();
        }
    }

    public void setStrokeWidth(float width) {
        if (width > 0 && this.strokeWidth != width) {
            this.strokeWidth = width;
            drawPaint.setStrokeWidth(width);
            postInvalidate();
        }
    }

    public void setGapAndWidth(int gap, int width) {
        boolean changed = false;
        if (width > 0 && width != dashedWidth) {
            dashedWidth = width;
            changed = true;
        }
        if (gap > 0 && dashedGap != gap) {
            dashedGap = gap;
            changed = true;
        }
        if (changed) {
            drawPaint.setPathEffect(new DashPathEffect(new float[]{dashedWidth, dashedGap}, 0));
            postInvalidate();
        }
    }

}
