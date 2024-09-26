package com.zpf.aaa.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView

class PinyinView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    private val linePaint = Paint()

    init {
        linePaint.setColor(Color.parseColor("#8004ABF1"))
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 1f * resources.displayMetrics.density
    }

    override fun onTextChanged(
        text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        var i = 0
        text?.forEach {
            if (it == ' ') {
                i++
            }
        }
        textSize = if (i > 2) {
            24f
        } else {
            36f
        }
    }

    override fun onDraw(canvas: Canvas) {
        val fontMetrics = paint.fontMetrics
        val eachSpace = (fontMetrics.descent - fontMetrics.ascent) / 2f
        val height = fontMetrics.bottom - fontMetrics.top
        val endX = measuredWidth.toFloat()
        val baseLineY: Float = when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.TOP -> {
                paddingTop + height - fontMetrics.bottom
            }
            Gravity.BOTTOM -> {
                measuredHeight - paddingBottom - fontMetrics.bottom
            }
            else -> {
                (measuredHeight - paddingBottom + paddingTop) * 0.5f + height * 0.5f - fontMetrics.bottom
            }
        }
        canvas.drawLine(0f, baseLineY - eachSpace * 2f, endX, baseLineY - eachSpace * 2f, linePaint)
        canvas.drawLine(0f, baseLineY - eachSpace, endX, baseLineY - eachSpace, linePaint)
        canvas.drawLine(0f, baseLineY, endX, baseLineY, linePaint)
        canvas.drawLine(0f, baseLineY + eachSpace, endX, baseLineY + eachSpace, linePaint)
        super.onDraw(canvas)
    }
}