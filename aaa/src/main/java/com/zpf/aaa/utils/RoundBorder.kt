package com.zpf.aaa.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import com.zpf.views.RoundCornerCuttingPoint

class RoundBorder(
    private val points: List<FloatArray>, private val n: Int, radius: Float
) {
    private val cornerList = ArrayList<RoundCornerCuttingPoint>(n)
    private val maxOutBound = RectF()
    private val path = Path()

    init {
        var p0: FloatArray
        var p1: FloatArray
        var p2: FloatArray
        for (i in 0 until n) {
            p0 = points[i]
            p1 = if (i < n - 1) {
                points[i + 1]
            } else {
                points[0]
            }
            p2 = if (i > 0) {
                points[i - 1]
            } else {
                points[n - 1]
            }
            cornerList.add(
                RoundCornerCuttingPoint(
                    p0[0], p0[1], p1[0], p1[1], p2[0], p2[1], radius
                )
            )
            if (i == 0) {
                maxOutBound.set(p0[0], p0[1], p0[0], p0[1])
            } else {
                if (maxOutBound.left > p0[0]) {
                    maxOutBound.left = p0[0]
                }
                if (maxOutBound.right < p0[0]) {
                    maxOutBound.right = p0[0]
                }
                if (maxOutBound.top > p0[1]) {
                    maxOutBound.top = p0[1]
                }
                if (maxOutBound.bottom < p0[1]) {
                    maxOutBound.bottom = p0[1]
                }
            }
        }
        cornerList.forEachIndexed { i, it ->
            if (i == 0) {
                path.moveTo(it.point2.x, it.point2.y)
            } else {
                path.lineTo(it.point2.x, it.point2.y)
            }
            if (!it.arcRect.isEmpty) {
                path.arcTo(it.arcRect, it.startAngle, it.sweepAngle)
            }
            path.lineTo(it.point1.x, it.point1.y)
        }
        path.close()
    }

    fun isInMaxOutBound(x: Float, y: Float): Boolean {
        return x >= maxOutBound.left && x <= maxOutBound.right && y >= maxOutBound.top && y <= maxOutBound.bottom
    }

    fun isInMaxOutBound(rectF: RectF): Boolean {
        if (maxOutBound.bottom < rectF.top || maxOutBound.top > rectF.bottom || maxOutBound.left > rectF.right || maxOutBound.right < rectF.left) {
            return false
        }
        return true
    }

    fun isInRect(x: Float, y: Float): Boolean {
        var last = 0f
        var j: Int
        for (i in 0 until n) {
            j = if (i < n - 1) {
                i + 1
            } else {
                0
            }
            val a = calc(x, y, points[i][0], points[i][1], points[j][0], points[j][1])
            if (last == 0f) {
                last = a
            } else {
                if ((last > 0 && a < 0) || (last < 0 && a > 0)) {
                    return false

                }
            }
        }
        return true
    }

    fun drawPath(canvas: Canvas, paint: Paint) {
        canvas.drawPath(path, paint)
    }

    fun getPoint(i: Int): PointF? {
        if (i <= 0 || i >= n) {
            return null
        }
        return PointF(points[i][0], points[i][1])
    }

    private fun calc(x: Float, y: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return (x1 - x2) * (y - y2) - (y1 - y2) * (x - x2)
    }

}