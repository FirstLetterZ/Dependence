package com.zpf.views.helper;

import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.views.type.IDrawingCanvasModifier;
import com.zpf.views.type.IRoundView;

public class ViewRoundHelper implements IDrawingCanvasModifier, IRoundView {
    private float connerRadius;
    private boolean isCircle = false;
    private final RectF srcRectF = new RectF();
    private final Paint paint = new Paint();
    private ViewDrawingCanvas drawingCanvas;

    public ViewRoundHelper() {
        paint.setAntiAlias(true);
    }
    @Override
    public void setDrawCircle(boolean circle) {
        this.isCircle = circle;
        this.connerRadius = 0f;
    }

    @Override
    public boolean isDrawCircle() {
        return isCircle;
    }
    @Override
    public void setConnerRadius(float radius) {
        this.connerRadius = radius;
        this.isCircle = false;
    }
    @Override
    public float getConnerRadius() {
        return connerRadius;
    }

    public void prepareCanvas(int width, int height) {
        final ViewDrawingCanvas oldCanvas = drawingCanvas;
        if (width <= 0 || height <= 0) {
            if (oldCanvas != null) {
                oldCanvas.bitmap.recycle();
            }
            drawingCanvas = null;
            return;
        }
        if (oldCanvas == null) {
            ViewDrawingCanvas newCanvas = new ViewDrawingCanvas(width, height);
            paint.setShader(new BitmapShader(newCanvas.bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            drawingCanvas = newCanvas;
        } else {
            if (oldCanvas.bitmap.isRecycled() || oldCanvas.bitmap.getWidth() != width || oldCanvas.bitmap.getHeight() != height) {
                oldCanvas.bitmap.recycle();
                ViewDrawingCanvas newCanvas = new ViewDrawingCanvas(width, height);
                paint.setShader(new BitmapShader(newCanvas.bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                drawingCanvas = newCanvas;
            }
        }
    }

    @Override
    public boolean ModifyCanvas(@NonNull Canvas canvas) {
        if (!isEnable()) {
            return false;
        }
        final ViewDrawingCanvas realCanvas = drawingCanvas;
        if (realCanvas == null) {
            return false;
        }
        srcRectF.left = 0;
        srcRectF.top = 0;
        srcRectF.right = realCanvas.bitmap.getWidth();
        srcRectF.bottom = realCanvas.bitmap.getHeight();
        if (isCircle) {
            float radius = Math.min(srcRectF.width(), srcRectF.height()) * 0.5f;
            canvas.drawCircle(srcRectF.centerX(), srcRectF.centerY(), radius, paint);
        } else if (connerRadius > 0) {
            canvas.drawRoundRect(srcRectF, connerRadius, connerRadius, paint);
        } else {
            canvas.drawBitmap(realCanvas.bitmap, 0, 0, null);
        }
        return true;
    }

    @Nullable
    public ViewDrawingCanvas getDrawingCanvas() {
        if (isEnable()) {
            return drawingCanvas;
        }
        return null;
    }

    public boolean isEnable() {
        return isCircle || connerRadius > 0f;
    }
}
