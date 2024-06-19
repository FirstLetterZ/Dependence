package com.zpf.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//todo zpf 圆角算法待优化
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
    private int direction = Direction.TOP;
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
        paint.setStrokeWidth(4);
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
        if (direction == Direction.LEFT) {//向左
            x0 = 0f;
            y0 = height * 0.5f;
            x1 = width;
            y1 = 0f;
            x2 = width;
            y2 = height;
        } else if (direction == Direction.TOP) {//向上
            x0 = width * 0.5f;
            y0 = 0f;
            x1 = width;
            y1 = height;
            x2 = 0f;
            y2 = height;
        } else if (direction == Direction.RIGHT) {//向右
            x0 = width;
            y0 = height * 0.5f;
            x1 = 0f;
            y1 = 0f;
            x2 = 0f;
            y2 = height;
        } else if (direction == Direction.BOTTOM) {//向下
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
            double a = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
            double b = Math.sqrt((x2 - x0) * (x2 - x0) + (y2 - y0) * (y2 - y0));
            double c = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            double angleTop = Math.acos((a * a + b * b - c * c) / (2 * a * b));
            double angleBottom = (Math.PI - angleTop) / 2.0;
            float maxRadius = (float) (2 * b * a * Math.sin(angleTop) / (a + b + c));
            float radius;
            RectF[] dots = new RectF[3];
            radius = Math.min(topRadius, maxRadius);
            if (radius > 0) {
                dots[0] = calcOval(x0, y0, x2, y2, x1, y1, b, a, radius, angleTop, true, path);
            } else {
                path.moveTo(x0, y0);
            }
            radius = Math.min(bottomRadius, maxRadius);
            if (radius > 0) {
                dots[1] = calcOval(x1, y1, x0, y0, x2, y2, a, c, radius, angleBottom, false, path);
                dots[2] = calcOval(x2, y2, x1, y1, x0, y0, c, b, radius, angleBottom, false, path);
            } else {
                path.lineTo(x1, y1);
                path.lineTo(x2, y2);
            }
            path.close();
            for (RectF rectF : dots) {
                if (rectF != null) {
                    path.moveTo(rectF.centerX(), rectF.centerY());
                    Log.e("ZPF", "rectF==>" + rectF.toString());
                    if (direction == Direction.RIGHT) {
                        path.addOval(rectF, Path.Direction.CCW);
                    } else {
                        path.addOval(rectF, Path.Direction.CW);
                    }
                }
            }

        } else {
            path.moveTo(x0, y0);
            path.lineTo(x1, y1);
            path.lineTo(x2, y2);
            path.close();
        }
    }

    private RectF calcOval(float x0, float y0, float x1, float y1, float x2, float y2, double len1, double len2, float radius, double angle, boolean firstPoint, Path path) {
        double len = radius / Math.tan(angle / 2.0);

        double ration = len / len1;
        float startX = (float) ((x1 - x0) * ration + x0);
        float startY = (float) ((y1 - y0) * ration + y0);

        ration = len / len2;
        float endX = (float) ((x2 - x0) * ration + x0);
        float endY = (float) ((y2 - y0) * ration + y0);
        float cx;
        float cy;
        if (firstPoint) {
            path.moveTo(startX, startY);
            float px = (x1 + x2) * 0.5f;
            float py = (y1 + y2) * 0.5f;
            double d = Math.sqrt((px - x0) * (px - x0) + (py - y0) * (py - y0));
            ration = radius / Math.sin(angle / 2.0) / d;
            cx = (float) ((px - x0) * ration + x0);
            cy = (float) ((py - y0) * ration + y0);
        } else {
            path.lineTo(startX, startY);
            PointF center = calcPoint(x0, y0, startX, startY, endX, endY);
            cx = center.x;
            cy = center.y;
        }
        path.lineTo(endX, endY);
        return new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
    }

    //todo zpf
    private PointF calcPoint(float x0, float y0, float x1, float y1, float x2, float y2) {
        Log.e("ZPF", "calcPoint==>x0=" + x0 + ";y0=" + y0 + ";x1=" + x1 + ";y1=" + y1 + ";x2=" + x2 + ";y2=" + y2);
        PointF point = new PointF();
//        (x1 - x0) * (x1 - point.x) + (y1 - y0) * (y1 - point.y) = 0;
//        (x2 - x0) * (x2 - point.x) + (y2 - y0) * (y2 - point.y) = 0;
        if (y1 == y0) {
            point.x = x1;
            point.y = (x2 - x0) * (x2 - x1) / (y2 - y0) + y2;
        } else if (x1 == x0) {
            point.x = (y2 - y0) * (y2 - y1) / (x2 - x0) + x2;
            point.y = y1;
        } else if (y2 == y0) {
            point.x = x2;
            point.y = (x1 - x0) * (x1 - x2) / (y1 - y0) + y1;
        } else if (x2 == x0) {
            point.x = (y1 - y0) * (y1 - y2) / (x1 - x0) + x1;
            point.y = y2;
        } else {
            point.x = x1 - ((y1 - y0) * (x2 - x0) * x2 + (y2 - y0) * (y2 - y1) * (y1 - y0)) / ((y2 - y0) * (x1 - x0));
            point.y = y1 + ((x1 - x0) * x1 - (x1 - x0) * point.x) / (y1 - y0);
        }
        return point;
    }

}