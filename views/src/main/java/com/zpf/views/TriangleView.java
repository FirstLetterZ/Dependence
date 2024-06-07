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
    private int direction = Direction.TOP;

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
        initValue(context.obtainStyledAttributes(attrs, R.styleable.TriangleView));

    }

    private void initValue(TypedArray typedArray) {
        if (typedArray != null) {
            int color = typedArray.getColor(R.styleable.TriangleView_triangle_color, Color.BLACK);
            direction = typedArray.getColor(R.styleable.TriangleView_triangle_direction, Direction.TOP);
            typedArray.recycle();
            paint.setColor(color);
        } else {
            paint.setColor(Color.BLACK);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        if (direction == Direction.LEFT) {//向左
            path.moveTo(0, getHeight() * 0.5f);
            path.lineTo(getWidth(), getHeight());
            path.lineTo(getWidth(), 0);
            path.close();
        } else if (direction == Direction.TOP) {//向上
            path.moveTo(getWidth() * 0.5f, 0);
            path.lineTo(getWidth(), getHeight());
            path.lineTo(0, getHeight());
            path.close();
        } else if (direction == Direction.RIGHT) {//向右
            path.moveTo(0, 0);
            path.lineTo(0, getHeight());
            path.lineTo(getWidth(), getHeight() * 0.5f);
            path.close();
        } else if (direction == Direction.BOTTOM) {//向下
            path.moveTo(0, 0);
            path.lineTo(getWidth(), 0);
            path.lineTo(getWidth() * 0.5f, getHeight());
            path.close();
        }
        canvas.drawPath(path, paint);
    }

    public void setTriangleColor(@ColorInt int color) {
        paint.setColor(color);
    }

    public int getTriangleColor() {
        return paint.getColor();
    }

    public void setDirection(int d) {
        direction = Math.abs(d % 4);
    }

    public int getDirection() {
        return direction;
    }

}