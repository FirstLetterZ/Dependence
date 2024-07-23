package com.zpf.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TriangleView extends View {
    @IntDef({
            Direction.LEFT,
            Direction.TOP,
            Direction.RIGHT,
            Direction.BOTTOM
    })
    public @interface Direction {
        int LEFT = 0;
        int TOP = 1;
        int RIGHT = 2;
        int BOTTOM = 3;
    }

    private final Path path;
    private final Paint paint;
    private int direction = Direction.BOTTOM;
    private float topRadius;
    private float bottomRadius;

    public TriangleView(Context context) {
        this(context, null, 0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
//        paint.setStrokeWidth(4);
//        paint.setStyle(Paint.Style.STROKE);
        initValue(context.obtainStyledAttributes(attrs, R.styleable.TriangleView));
    }

    private void initValue(TypedArray typedArray) {
        if (typedArray != null) {
            int color = typedArray.getColor(R.styleable.TriangleView_triangle_color, Color.BLACK);
            direction = typedArray.getColor(R.styleable.TriangleView_triangle_direction, Direction.TOP);
            topRadius = typedArray.getDimension(R.styleable.TriangleView_top_radius, 0f);
            bottomRadius = typedArray.getDimension(R.styleable.TriangleView_bottom_radius, 0f);
            typedArray.recycle();
            paint.setColor(color);
        } else {
            paint.setColor(Color.BLACK);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        preparePath();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    public void setTriangleColor(@ColorInt int color) {
        paint.setColor(color);
    }

    public int getTriangleColor() {
        return paint.getColor();
    }

    public void setDirection(int d) {
        int newDirection = Math.abs(d % 4);
        if (direction == newDirection) {
            return;
        }
        direction = newDirection;
        preparePath();
    }

    public int getDirection() {
        return direction;
    }

    public void setCornerRadius(float topRadius, float bottomRadius) {
        if (topRadius == this.topRadius && bottomRadius == this.bottomRadius) {
            return;
        }
        this.topRadius = topRadius;
        this.bottomRadius = bottomRadius;
        preparePath();
    }

    private void preparePath() {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        path.reset();
        float x0;
        float y0;
        float x1;
        float y1;
        float x2;
        float y2;
        int realDirection = direction % 4;
        if (realDirection == Direction.LEFT) {//向左
            x0 = 0f;
            y0 = height * 0.5f;
            x1 = width;
            y1 = 0f;
            x2 = width;
            y2 = height;
        } else if (realDirection == Direction.TOP) {//向上
            x0 = width * 0.5f;
            y0 = 0f;
            x1 = width;
            y1 = height;
            x2 = 0f;
            y2 = height;
        } else if (realDirection == Direction.RIGHT) {//向右
            x0 = width;
            y0 = height * 0.5f;
            x1 = 0f;
            y1 = height;
            x2 = 0f;
            y2 = 0f;
        } else if (realDirection == Direction.BOTTOM) {//向下
            x0 = width * 0.5f;
            y0 = height;
            x1 = 0f;
            y1 = 0f;
            x2 = width;
            y2 = 0f;
        } else {
            return;
        }
        if (topRadius > 0f || bottomRadius > 0f) {
            RoundCornerCuttingPoint p0 = new RoundCornerCuttingPoint(x0, y0, x1, y1, x2, y2, topRadius);
            RoundCornerCuttingPoint p1 = new RoundCornerCuttingPoint(x1, y1, x2, y2, x0, y0, bottomRadius);
            RoundCornerCuttingPoint p2 = new RoundCornerCuttingPoint(x2, y2, x0, y0, x1, y1, bottomRadius);
            path.moveTo(p0.point2.x, p0.point2.y);
            if (!p0.arcRect.isEmpty()) {
                path.arcTo(p0.arcRect, p0.startAngle, p0.sweepAngle);
            }
            path.lineTo(p0.point1.x, p0.point1.y);
            path.lineTo(p1.point2.x, p1.point2.y);
            if (!p1.arcRect.isEmpty()) {
                path.arcTo(p1.arcRect, p1.startAngle, p1.sweepAngle);
            }
            path.lineTo(p1.point1.x, p1.point1.y);
            path.lineTo(p2.point2.x, p2.point2.y);
            if (!p2.arcRect.isEmpty()) {
                path.arcTo(p2.arcRect, p2.startAngle, p2.sweepAngle);
            }
            path.lineTo(p2.point1.x, p2.point1.y);
            path.close();
        } else {
            path.moveTo(x0, y0);
            path.lineTo(x1, y1);
            path.lineTo(x2, y2);
            path.close();
        }
    }

}