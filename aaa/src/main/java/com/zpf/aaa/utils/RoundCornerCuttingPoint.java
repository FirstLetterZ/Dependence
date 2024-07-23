package com.zpf.aaa.utils;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

public class RoundCornerCuttingPoint {
    public final PointF pointVertex;
    public final PointF point1;
    public final PointF point2;
    public final PointF pointCenter;
    public final RectF arcRect;
    public final float startAngle;
    public final float sweepAngle;

    /**
     * @param x0     顶点
     * @param y0     顶点
     * @param x1     顺时针第一点
     * @param y1     顺时针第一点
     * @param x2     逆时针第一点
     * @param y2     逆时针第一点
     * @param radius 圆角半径
     */
    public RoundCornerCuttingPoint(float x0, float y0, float x1, float y1, float x2, float y2, float radius) {
        pointVertex = new PointF(x0, y0);
        Log.e("ZPF", "onCreate==>x0=" + x0 + ";y0=" + y0 + ";x1=" + x1 + ";y1=" + y1 + ";x2=" + x2 + ";y2=" + y2 + ";radius=" + radius);
        double a = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        double b = Math.sqrt((x2 - x0) * (x2 - x0) + (y2 - y0) * (y2 - y0));
        double c = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        double angle = Math.acos((a * a + b * b - c * c) / (2 * a * b));
        double len = radius / Math.tan(angle / 2.0);
        double ration = len / a;
        point1 = new PointF((float) ((x1 - x0) * ration + x0), (float) ((y1 - y0) * ration + y0));
        Log.e("ZPF", "point1==>" + point1.x + "," + point1.y);

        ration = len / b;
        point2 = new PointF((float) ((x2 - x0) * ration + x0), (float) ((y2 - y0) * ration + y0));
        Log.e("ZPF", "point2==>" + point2.x + "," + point2.y);

        pointCenter = calcVerticalLinesPoint(x0, y0, point1.x, point1.y, point2.x, point2.y, angle, radius);
        Log.e("ZPF", "pointCenter==>" + pointCenter.x + "," + pointCenter.y);

        arcRect = new RectF(pointCenter.x - radius, pointCenter.y - radius, pointCenter.x + radius, pointCenter.y + radius);
        angle = calcVertexAngle(pointCenter.x, pointCenter.y, point2.x, point2.y, pointCenter.x + radius, pointCenter.y);
        if (point2.y > pointCenter.y) {
            startAngle = (float) Math.toDegrees(angle);
        } else {
            startAngle = (float) Math.toDegrees(Math.PI * 2 - angle);
        }
        angle = calcVertexAngle(pointCenter.x, pointCenter.y, point1.x, point1.y, point2.x, point2.y);
        sweepAngle = (float) Math.toDegrees(angle);
        Log.e("ZPF", "startAngle=" + startAngle + ";sweepAngle=" + sweepAngle);

    }

    private PointF calcVerticalLinesPoint(
            float x0, float y0, float x1, float y1, float x2, float y2, double angle, float radius) {
        PointF point = new PointF();

        if (x0 == x1) {
            if (x2 > x1) {
                point.x = x1 + radius;
            } else {
                point.x = x1 - radius;
            }
            point.y = y1;
        } else if (x0 == x2) {
            if (x1 > x2) {
                point.x = x2 + radius;
            } else {
                point.x = x2 - radius;
            }
            point.y = y2;
        } else if (y1 == y0) {
            point.x = x1;
            if (y2 > y1) {
                point.y = y1 + radius;
            } else {
                point.y = y1 - radius;
            }
        } else if (y2 == y0) {
            point.x = x2;
            if (y1 > y2) {
                point.y = y2 + radius;
            } else {
                point.y = y2 - radius;
            }
        } else if (x1 == x2) {
            float dx = (float) (radius / Math.sin(angle / 2));
            if (x1 > x0) {
                point.x = x0 + dx;
            } else {
                point.x = x0 - dx;
            }
            point.y = (y1 + y2) * 0.5f;
        } else if (y1 == y2) {
            point.x = (x1 + x2) * 0.5f;
            float dy = (float) (radius / Math.sin(angle / 2));
            if (y1 > y0) {
                point.y = y0 + dy;
            } else {
                point.y = y0 - dy;
            }
        } else {//todo zpf 待优化 精度溢出？
            point.x = x1 - ((y1 - y0) * (x2 - x0) * x2 + (y2 - y0) * (y2 - y1) * (y1 - y0)) / ((y2 - y0) * (x1 - x0));
            point.y = y1 + ((x1 - x0) * x1 - (x1 - x0) * point.x) / (y1 - y0);
        }
        return point;
    }

    private double calcVertexAngle(float x0, float y0, float x1, float y1, float x2, float y2) {
        double a = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        double b = Math.sqrt((x2 - x0) * (x2 - x0) + (y2 - y0) * (y2 - y0));
        double c = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return Math.acos((a * a + b * b - c * c) / (2 * a * b));
    }

}
