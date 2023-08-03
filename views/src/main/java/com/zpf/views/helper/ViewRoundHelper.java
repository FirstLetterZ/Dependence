package com.zpf.views.helper;

import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.NonNull;

import com.zpf.views.type.IDrawingCanvasModifier;
import com.zpf.views.ViewDrawingCanvas;

public class ViewRoundHelper implements IDrawingCanvasModifier {
    private float connerRadius;
    private boolean isCircle = false;
    private final RectF srcRectF = new RectF();
    private final Path path = new Path();
    private final Paint paint = new Paint();
    private ViewDrawingCanvas drawingCanvas;

    public ViewRoundHelper() {
        paint.setAntiAlias(true);
    }

    public void setDrawCircle(boolean circle) {
        this.isCircle = circle;
    }

    public void setConnerRadius(float radius) {
        this.connerRadius = radius;
        this.isCircle = false;
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

    public ViewDrawingCanvas getDrawingCanvas() {
        return drawingCanvas;
    }
}
