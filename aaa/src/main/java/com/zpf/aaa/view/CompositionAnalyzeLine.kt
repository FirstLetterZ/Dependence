package com.zpf.aaa.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class CompositionAnalyzeLine @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val density = resources.displayMetrics.density
    private val topBottomHeightDiff = -5 * density
    private val middleLineHeightDiff = 10 * density
    private val topItemSpace = 7 * density
    private val bottomItemSpace = 13 * density
    private val bottomRadius = 12 * density
    private val bottomLineHeight = 20 * density
    private val paint = Paint()
    private val color1 = Color.parseColor("#04ABF1")
    private val color2 = Color.parseColor("#33E338")
    private val color3 = Color.parseColor("#EAF515")
    private val color4 = Color.parseColor("#FFB216")
    private val color5 = Color.parseColor("#FB466D")
    private var middlePoint = PointF()
    private val path1 = Path()
    private val path2 = Path()
    private val path3 = Path()
    private val path4 = Path()
    private val path5 = Path()

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8 * density
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val topItemWidth = (measuredWidth - topItemSpace - topItemSpace) / 3f
        val bottomItemWidth = (measuredWidth - bottomItemSpace) / 2f
        middlePoint.set(measuredWidth * 0.5f, (measuredHeight + topBottomHeightDiff) * 0.5f)
        path2.reset()
        path2.moveTo(middlePoint.x, middlePoint.y)
        path2.lineTo(middlePoint.x, 0f)

        var x1 = topItemWidth
        var y1 = middlePoint.y
        var x2 = topItemWidth * 0.5f
        var y2 = middleLineHeightDiff
        var x3 = x2
        var y3 = middlePoint.y
        path1.reset()
        path1.moveTo(middlePoint.x, middlePoint.y)
        path1.lineTo(x1, y1)
        path1.quadTo(x3, y3, x2, y2)

        x1 = measuredWidth - topItemWidth
        x2 = measuredWidth - topItemWidth * 0.5f
        x3 = x2
        path3.moveTo(middlePoint.x, middlePoint.y)
        path3.lineTo(x1, y1)
        path3.quadTo(x3, y3, x2, y2)

        x1 = middlePoint.x - paint.strokeWidth * 0.6f
        y1 = middlePoint.y + bottomLineHeight
        x2 = bottomItemWidth * 0.5f
        y2 = measuredHeight.toFloat()
        x3 = (x1 + x2) * 0.5f
        y3 = (y1 + y2) * 0.5f
        path4.reset()
        path4.moveTo(x1, middlePoint.y)
        path4.lineTo(x1, y1)
        path4.quadTo(x1, y1 + bottomRadius, x3, y3)
        path4.quadTo(x2, y2 - bottomRadius, x2, y2)

        x1 = middlePoint.x + paint.strokeWidth * 0.6f
        x2 = measuredWidth - bottomItemWidth * 0.5f
        x3 = (x1 + x2) * 0.5f
        y3 = (y1 + y2) * 0.5f
        path5.reset()
        path5.moveTo(x1, middlePoint.y)
        path5.lineTo(x1, y1)
        path5.quadTo(x1, y1 + bottomRadius, x3, y3)
        path5.quadTo(x2, y2 - bottomRadius, x2, y2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.setColor(color1)
        canvas.drawPath(path1, paint)
        paint.setColor(color2)
        canvas.drawPath(path2, paint)
        paint.setColor(color3)
        canvas.drawPath(path3, paint)
        paint.setColor(color4)
        canvas.drawPath(path4, paint)
        paint.setColor(color5)
        canvas.drawPath(path5, paint)
    }

}