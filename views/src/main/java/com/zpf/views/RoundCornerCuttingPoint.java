package com.zpf.views;

import android.graphics.PointF;
import android.graphics.RectF;

import androidx.annotation.NonNull;

public class RoundCornerCuttingPoint {
    public final PointF pointVertex;
    public final PointF point1;
    public final PointF point2;
    public final PointF pointCenter;
    public final RectF arcRect;
    public final float startAngle;
    public final float sweepAngle;
    public final float vertexAngle;

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
        double a = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        double b = Math.sqrt((x2 - x0) * (x2 - x0) + (y2 - y0) * (y2 - y0));
        double c = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        double angle = Math.acos((a * a + b * b - c * c) / (2 * a * b));
        vertexAngle = (float) angle;
        if (radius > 0) {
            double len = radius / Math.tan(angle / 2.0);
            double ration = len / a;
            point1 = new PointF((float) ((x1 - x0) * ration + x0), (float) ((y1 - y0) * ration + y0));
            ration = len / b;
            point2 = new PointF((float) ((x2 - x0) * ration + x0), (float) ((y2 - y0) * ration + y0));
            pointCenter = calcVerticalLinesPoint(x0, y0, point1.x, point1.y, point2.x, point2.y, angle);
            arcRect = new RectF(pointCenter.x - radius, pointCenter.y - radius, pointCenter.x + radius, pointCenter.y + radius);
            angle = calcVertexAngle(pointCenter.x, pointCenter.y, point2.x, point2.y, pointCenter.x + radius, pointCenter.y);
            if (point2.y > pointCenter.y) {
                startAngle = (float) Math.toDegrees(angle);
            } else {
                startAngle = (float) Math.toDegrees(Math.PI * 2 - angle);
            }
            angle = calcVertexAngle(pointCenter.x, pointCenter.y, point1.x, point1.y, point2.x, point2.y);
            sweepAngle = (float) Math.toDegrees(angle);
        } else {
            point1 = new PointF(x0, y0);
            point2 = new PointF(x0, y0);
            pointCenter = new PointF(x0, y0);
            arcRect = new RectF();
            startAngle = 0f;
            sweepAngle = 0f;
        }
    }

    /**
     * @param x0    顶点
     * @param y0    顶点
     * @param x1    切点1
     * @param y1    切点1
     * @param x2    切点2
     * @param y2    切点2
     * @param angle 顶点夹角
     * @return 圆心坐标
     */
    private PointF calcVerticalLinesPoint(float x0, float y0, float x1, float y1, float x2, float y2, double angle) {
        double mx = (x1 + x2) / 2.0;
        double my = (y1 + y2) / 2.0;
        double cv = Math.cos(angle / 2.0);
        double ration = 1 / (cv * cv);
        return new PointF((float) ((mx - x0) * ration + x0), (float) ((my - y0) * ration + y0));
    }

    private double calcVertexAngle(float x0, float y0, float x1, float y1, float x2, float y2) {
        double a = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        double b = Math.sqrt((x2 - x0) * (x2 - x0) + (y2 - y0) * (y2 - y0));
        double c = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return Math.acos((a * a + b * b - c * c) / (2 * a * b));
    }

    @NonNull
    @Override
    public String toString() {
        return "{" +
                "pointVertex=" + pointVertex +
                ", point1=" + point1 +
                ", point2=" + point2 +
                ", pointCenter=" + pointCenter +
                ", arcRect=" + arcRect +
                ", startAngle=" + startAngle +
                ", sweepAngle=" + sweepAngle +
                ", vertexAngle=" + vertexAngle +
                '}';
    }
}
