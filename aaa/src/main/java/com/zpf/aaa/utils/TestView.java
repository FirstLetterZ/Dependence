package com.zpf.aaa.utils;

import android.content.Context;
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

//todo zpf 圆角算法待优化
public class TestView extends View {
    @IntDef({Direction.LEFT, Direction.TOP, Direction.RIGHT, Direction.BOTTOM})
    public @interface Direction {
        int LEFT = 0;
        int TOP = 1;
        int RIGHT = 2;
        int BOTTOM = 3;
    }

    private final Path path;
    private final Paint paint;
    private int direction = Direction.LEFT;
    private float radius;
    //    private float bottomRadius;
    private RoundCornerCuttingPoint pointInfo1;
    private RoundCornerCuttingPoint pointInfo2;
    private RoundCornerCuttingPoint pointInfo3;
    public TestView(Context context) {
        this(context, null, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        radius = getResources().getDisplayMetrics().density * 8f;
//        initValue(context.obtainStyledAttributes(attrs, com.zpf.views.R.styleable.TriangleView));
    }

//    private void initValue(TypedArray typedArray) {
//        if (typedArray != null) {
//            int color = typedArray.getColor(com.zpf.views.R.styleable.TriangleView_triangle_color, Color.BLACK);
//            direction = typedArray.getColor(com.zpf.views.R.styleable.TriangleView_triangle_direction, Direction.TOP);
//            topRadius = typedArray.getDimension(com.zpf.views.R.styleable.TriangleView_top_radius, 0f);
//            bottomRadius = typedArray.getDimension(com.zpf.views.R.styleable.TriangleView_bottom_radius, 0f);
//            typedArray.recycle();
//            paint.setColor(color);
//        } else {
//            paint.setColor(Color.BLACK);
//        }
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        preparePath();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
        paint.setColor(Color.BLACK);
        if (pointInfo1 != null) {
//            canvas.drawArc(pointInfo1.center,pointInfo1.startAngle,pointInfo1.sweepAngle,false,paint);
            canvas.drawOval(pointInfo1.point1.x - 2f, pointInfo1.point1.y - 2f, pointInfo1.point1.x + 2f, pointInfo1.point1.y + 2f, paint);
            canvas.drawOval(pointInfo1.point2.x - 2f, pointInfo1.point2.y - 2f, pointInfo1.point2.x + 2f, pointInfo1.point2.y + 2f, paint);
        }
        if (pointInfo2 != null) {
//            canvas.drawArc(pointInfo2.center,pointInfo2.startAngle,pointInfo2.sweepAngle,false,paint);

            canvas.drawOval(pointInfo2.point1.x - 2f, pointInfo2.point1.y - 2f, pointInfo2.point1.x + 2f, pointInfo2.point1.y + 2f, paint);
            canvas.drawOval(pointInfo2.point2.x - 2f, pointInfo2.point2.y - 2f, pointInfo2.point2.x + 2f, pointInfo2.point2.y + 2f, paint);
        }
        if (pointInfo3 != null) {
//            canvas.drawArc(pointInfo3.center,pointInfo3.startAngle,pointInfo3.sweepAngle,false,paint);

            canvas.drawOval(pointInfo3.point1.x - 2f, pointInfo3.point1.y - 2f, pointInfo3.point1.x + 2f, pointInfo3.point1.y + 2f, paint);
            canvas.drawOval(pointInfo3.point2.x - 2f, pointInfo3.point2.y - 2f, pointInfo3.point2.x + 2f, pointInfo3.point2.y + 2f, paint);
        }
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

//    public void setCornerRadius(float topRadius, float bottomRadius) {
//        if (topRadius == this.topRadius && bottomRadius == this.bottomRadius) {
//            return;
//        }
//        this.topRadius = topRadius;
//        this.bottomRadius = bottomRadius;
//        preparePath();
//    }

    private void preparePath() {
        float delta = paint.getStrokeWidth();
        float width = getWidth() - delta * 2;
        float height = getHeight() - delta * 2;
        if (width <= 0f || height <= 0f) {
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
            x0 = delta;
            y0 = height * 0.5f + delta;
            x1 = width + delta;
            y1 = delta;
            x2 = width + delta;
            y2 = height + delta;
        } else if (realDirection == Direction.TOP) {//向上
            x0 = width * 0.5f + delta;
            y0 = delta;
            x1 = width + delta;
            y1 = height + delta;
            x2 = delta;
            y2 = height + delta;
        } else if (realDirection == Direction.RIGHT) {//向右
            x0 = width + delta;
            y0 = height * 0.5f + delta;
            x1 = delta;
            y1 = delta;
            x2 = delta;
            y2 = height + delta;
        } else if (realDirection == Direction.BOTTOM) {//向下
            x0 = width * 0.5f + delta;
            y0 = height + delta;
            x1 = delta;
            y1 = delta;
            x2 = width + delta;
            y2 = delta;
        } else {
            return;
        }
        if (radius > 0f) {

            pointInfo1 = new RoundCornerCuttingPoint(x0, y0, x1, y1, x2, y2, radius);
            pointInfo2 = new RoundCornerCuttingPoint(x1, y1, x2, y2, x0, y0, radius);
            pointInfo3 = new RoundCornerCuttingPoint(x2, y2, x0, y0, x1, y1, radius);
            path.reset();
            path.moveTo(pointInfo1.point1.x, pointInfo1.point1.y);
            path.lineTo(pointInfo2.point2.x, pointInfo2.point2.y);
//            path.addArc(pointInfo2.center, pointInfo2.startAngle, pointInfo1.sweepAngle);
//            path.addOval(pointInfo2.center,Path.Direction.CW);

            path.moveTo(pointInfo2.point1.x, pointInfo2.point1.y);
            path.lineTo(pointInfo3.point2.x, pointInfo3.point2.y);
//            path.addOval(pointInfo3.center,Path.Direction.CW);
//            path.addArc(pointInfo3.center, pointInfo3.startAngle, pointInfo1.sweepAngle);

            path.moveTo(pointInfo3.point1.x, pointInfo3.point1.y);
            path.lineTo(pointInfo1.point2.x, pointInfo1.point2.y);
//            path.addOval(pointInfo1.center,Path.Direction.CW);
//            path.addArc(pointInfo1.center, pointInfo1.startAngle, pointInfo1.sweepAngle);

        } else {
            path.moveTo(x0, y0);
            path.lineTo(x1, y1);
            path.lineTo(x2, y2);
            path.close();
        }
    }

}