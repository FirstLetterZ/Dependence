package com.zpf.tool.func;

import android.graphics.PointF;

import androidx.annotation.Nullable;

/**
 * x2*x^2 + x1*x + y2*y^2 + y1*y + a*xy+ c = 0
 */
public class TwoDimensionalFunction {
    public final float x1;
    public final float x2;
    public final float y1;
    public final float y2;
    public final float a;
    public final float c;

    public TwoDimensionalFunction(float x1, float x2, float y1, float y2, float a, float c) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.a = a;
        this.c = c;
    }

    @Nullable
    public Float calcX(float yValue) {
        if (x1 == 0 && x2 == 0) {
            return null;
        }
        return (-c - y1 * yValue) / x1;
    }

    @Nullable
    public Float calcY(float xValue) {
        if (y1 == 0) {
            return null;
        }
        return (-c - x1 * xValue) / y1;
    }

    public boolean isInvalid() {
        return x1 == 0 && x2 == 0 && y1 == 0 && y2 == 0;
    }

    @Nullable
    public PointF crossPoint(TwoDimensionalFunction odf) {
        if (isInvalid() || odf.isInvalid()) {
            return null;
        }
        if (x1 == 0) {
            float pointY = -c / y1;
            Float pointX = odf.calcX(pointY);
            if (pointX == null) {
                return null;
            }
            return new PointF(pointX, pointY);
        }
        if (y1 == 0) {
            float pointX = -c / x1;
            Float pointY = odf.calcY(pointX);
            if (pointY == null) {
                return null;
            }
            return new PointF(pointX, pointY);
        }

        float a = odf.y1 - odf.x1 * y1 / x1;
        if (a == 0f) {
            return null;
        }
        float pointY = (-odf.c - odf.x1 / x1 * c) / a;
        Float pointX = odf.calcX(pointY);
        if (pointX == null) {
            return null;
        }
        return new PointF(pointX, pointY);
    }
}