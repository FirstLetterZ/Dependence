package com.zpf.aaa.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.appcompat.widget.AppCompatImageView

class SelectorImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var downX: Float = 0f
    private var downY: Float = 0f
    private var downTime: Long = 0L
    private var isMoving: Boolean = false
    private val paint = Paint()
    private val selectRect = RectF()
    private val boxRadius: Float
    private val boxBorder: Float
    private val textBorder: Float

    init {
        val dm = resources.displayMetrics
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        boxRadius = dm.density
        boxBorder = dm.density * 1f
        textBorder = dm.density * 1.5f
    }

    private var normalBorderColor: Int = Color.RED
    private var normalRectColor: Int = Color.TRANSPARENT
    private var selectBorderColor: Int = Color.BLUE
    private var selectRectColor: Int = Color.TRANSPARENT
    val dataList = ArrayList<TextRect>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val imgContent = drawable
        if (imgContent == null || imgContent.intrinsicWidth == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            val mw = MeasureSpec.getSize(widthMeasureSpec)
            val mh = imgContent.intrinsicHeight.toFloat() / imgContent.intrinsicWidth * mw
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(mw, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mh.toInt(), MeasureSpec.EXACTLY)
            )
        }
        calcDrawBox()
    }

    fun setTextList(list: List<TextRect>?) {
        dataList.clear()
        if (list?.isNotEmpty() == true) {
            dataList.addAll(list)
        }
        calcDrawBox()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                downTime = System.currentTimeMillis()
                isMoving = false
                selectRect.set(0f, 0f, 0f, 0f)
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                if (!isMoving) {
                    val dx = Math.abs(x - downX)
                    val dy = Math.abs(y - downY)
                    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
                    if (dx > touchSlop || dy > touchSlop) {
                        isMoving = true
                    }
                }
                if (isMoving) {
                    val left = Math.min(x, downX)
                    val top = Math.min(y, downY)
                    val right = Math.max(x, downX)
                    val bottom = Math.max(y, downY)
                    selectRect.set(left, top, right, bottom)
                    checkSelect(selectRect, false)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                val x = event.x
                val y = event.y
                if (isMoving) {
                    val left = Math.min(x, downX)
                    val top = Math.min(y, downY)
                    val right = Math.max(x, downX)
                    val bottom = Math.max(y, downY)
                    selectRect.set(left, top, right, bottom)
                    Log.e("ZPF", "ACTION_UP checkSelect==> ${selectRect}")
                    checkSelect(selectRect, true)
                } else {
                    if (System.currentTimeMillis() - downTime < 800L) {
                        Log.e("ZPF", "checkSelect==> click; ")
                        checkSelect(x, y)
                    }
                }
                selectRect.set(0f, 0f, 0f, 0f)
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                if (isMoving) {
                    selectRect.set(0f, 0f, 0f, 0f)
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!selectRect.isEmpty) {
            if (selectRectColor != 0) {
                paint.setColor(selectRectColor)
                paint.style = Paint.Style.FILL
                canvas.drawRoundRect(selectRect, boxRadius, boxRadius, paint)
            }
            if (selectBorderColor != 0) {
                paint.setColor(selectBorderColor)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = boxBorder
                canvas.drawRoundRect(selectRect, boxRadius, boxRadius, paint)
            }
        }
        if (drawable == null) {
            return
        }
        dataList.forEach { item ->
            item.drawInfo?.let { d ->
                if (item.select || item.tempSelect) {
                    if (selectRectColor != 0) {
                        paint.setColor(selectRectColor)
                        paint.style = Paint.Style.FILL
                        d.drawPath(canvas, paint)
                    }
                    if (selectBorderColor != 0) {
                        paint.setColor(selectBorderColor)
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = textBorder
                        d.drawPath(canvas, paint)
                    }
                } else {
                    if (normalRectColor != 0) {
                        paint.setColor(normalRectColor)
                        paint.style = Paint.Style.FILL
                        d.drawPath(canvas, paint)
                    }
                    if (normalBorderColor != 0) {
                        paint.setColor(normalBorderColor)
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth = textBorder
                        d.drawPath(canvas, paint)
                    }
                }
            }
        }
    }

    private fun checkSelect(rectF: RectF, end: Boolean) {
        for (item in dataList) {
            val d = item.drawInfo
            if (d != null) {
                if (d.isInMaxOutBound(rectF)) {
                    if (end) {
                        item.select = true
                        item.tempSelect = false
                    } else {
                        item.select = false
                        item.tempSelect = true
                    }
                } else {
                    item.tempSelect = false
                }
            }
        }
    }

    private fun checkSelect(x: Float, y: Float) {
        for (item in dataList) {
            val d = item.drawInfo
            if (d != null) {
                val a = d.isInMaxOutBound(x, y)
                val b = d.isInRect(x, y)
                Log.e("ZPF", "checkSelect==>click;${item.text} ;$a; $b")
                if (a && b) {
                    item.select = !item.select
                    Log.e("ZPF", "checkSelect==>click 1111111111;${item.text}")
                    break
                }
            }
        }
    }

    private fun calcDrawBox() {
        val mw = measuredWidth
        val mh = measuredHeight
        val imageWidth = drawable?.intrinsicWidth ?: 0
        val imageHeight = drawable?.intrinsicHeight ?: 0
        if (mw <= 0 || mh <= 0 || imageWidth <= 0 || imageHeight <= 0 || dataList.isEmpty()) {
            return
        }
        val scale: Float = mw.toFloat() / imageWidth.toFloat()
        dataList.forEach { data ->
            try {
                val pointList = data.polygons.map { p ->
                    floatArrayOf(p[0] * scale, p[1] * scale)
                }
                val border = RoundBorder(pointList, 4, boxRadius)
                data.drawInfo = border
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        invalidate()
    }
}