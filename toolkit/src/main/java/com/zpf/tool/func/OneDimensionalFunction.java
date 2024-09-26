package com.zpf.tool.func;

import android.graphics.PointF;

import androidx.annotation.Nullable;

/**
 * x1*x + y1*y + c = 0
 */
public class OneDimensionalFunction {
    public final float x1;
    public final float y1;
    public final float c;

    public OneDimensionalFunction(float x1, float y1, float c) {
        this.x1 = x1;
        this.y1 = y1;
        this.c = c;
    }

    @Nullable
    public Float calcX(float yValue) {
        if (x1 == 0) {
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
        return x1 == 0 && y1 == 0;
    }

    @Nullable
    public PointF crossPoint(OneDimensionalFunction odf) {
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