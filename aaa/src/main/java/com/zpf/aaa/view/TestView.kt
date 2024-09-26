package com.zpf.aaa.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class TestView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val maskPaint: Paint by lazy {
        val p = Paint()
        val lg = LinearGradient(
            0f,
            0f,
            200f,
            0f,
            Color.parseColor("#80000000"),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        p.setShader(lg)
        p
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        if (isTextMatching) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), maskPaint)
//        }
    }
}