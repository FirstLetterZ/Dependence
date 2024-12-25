package com.zpf.tool.func;

import android.graphics.PointF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    public List<Float> calcX(float yValue) {
        if (x1 == 0 && x2 == 0) {
            return null;
        }
        return solveEquation(x2, x1 + a * yValue, y2 * yValue * yValue + y1 * yValue + c);
    }

    @Nullable
    public List<Float> calcY(float xValue) {
        if (y1 == 0 && y2 == 0) {
            return null;
        }
        return solveEquation(y2, y1 + a * xValue, x2 * xValue * xValue + x1 * xValue + c);
    }

    @Nullable
    public List<PointF> crossPoint(TwoDimensionalFunction equation) {
        if (isInvalid() || equation.isInvalid()) {
            return null;
        }
        float temp;
        if (x2 != 0 && equation.x2 != 0) {
            temp = -x2 / equation.x2;
        } else if (x1 != 0 && equation.x1 != 0) {
            temp = -x1 / equation.x1;
        } else {
            return null;
        }
        float mx2 = equation.x2 * temp + x2;
        float mx1 = equation.x1 * temp + x1;
        if (mx2 != 0 || mx1 != 0) {
            return null;
        }
        List<Float> yList = solveEquation(equation.y2 * temp + y2, equation.y1 * temp + y1, equation.c * temp + c);
        if (yList == null) {
            return null;
        }
        List<PointF> result = new ArrayList<>();
        for (float yValue : yList) {
            List<Float> xList = calcX(yValue);
            if (xList != null) {
                for (float xValue : xList) {
                    result.add(new PointF(xValue, yValue));
                }
            }
        }
        return result;
    }

    public boolean checkResult(float x, float y, float accuracy) {
        float temp = x2 * x * x + x1 * x + y2 * y * y + y1 * y + a * x * y + c;
        return Math.abs(temp) < Math.abs(accuracy);
    }

    public boolean isInvalid() {
        return x1 == 0 && x2 == 0 && y1 == 0 && y2 == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return x2 + "*x^2 + " + x1 + "*x + " + y2 + "*y^2 + " + y1 + "*y + " + a + "*xy+ " + c + " = 0";
    }

    private List<Float> solveEquation(float a2, float a1, float a0) {
        if (a2 == 0) {
            if (a1 == 0) {
                return null;
            }
            ArrayList<Float> result = new ArrayList<>();
            result.add(-a0 / a1);
            return result;
        }
        float b1 = a1 / a2 / 2f;
        float b0 = 0f - a0 / a2 - (float) (Math.abs(b1));
        //(y±√b1)^2=b0
        if (b0 < 0f) {
            return null;
        }
        float b2;
        ArrayList<Float> result = new ArrayList<>();
        if (b1 < 0f) {
            b2 = (float) Math.sqrt(-b1);
        } else {
            b2 = -(float) Math.sqrt(b1);
        }
        if (b0 == 0f) {
            result.add(b2);
        } else {
            float c = (float) Math.sqrt(b0);
            result.add(c + b2);
            result.add(-c + b2);
        }
        return result;
    }
}