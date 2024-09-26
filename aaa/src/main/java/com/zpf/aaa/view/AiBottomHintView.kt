package com.zpf.aaa.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.MaskFilterSpan
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.clearSpans
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class AiBottomHintView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    private val maskInfo by lazy {
        TextMatchMask(resources)
    }
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var minScroll = 0
    private var maxScrollX = 0
    private var i = 0
    private var j = 0
    private var k = 5
    private var originalText: SpannableString? = null
    private val symbolList =
        charArrayOf(',', '，', '.', '。', ';', '；', '?', '？', '!', '！', '~', '～', '…', '、')

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.UNSPECIFIED),
            heightMeasureSpec
        )
        minScroll = min((measuredWidth - maxWidth) / 2, 0)
        maxScrollX = max(0, measuredWidth - maxWidth)
        setMeasuredDimension(maxWidth, measuredHeight)
        scrollTo(minScroll, 0)
        Log.e("ZPF", "minScroll=$minScroll;maxScrollX=$maxScrollX")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (originalText?.isNotEmpty() == true) {
            canvas.save()
            canvas.translate(scrollX.toFloat(), 0f)
            canvas.drawRect(0f, 0f, maskInfo.maskWidth, height.toFloat(), maskInfo.maskPaint)
            canvas.restore()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = lastX - event.x
                val dy = lastY - event.y
                if (abs(dx) > abs(dy)) {
                    scrollBy(dx.toInt(), 0)
                }
                lastX = event.x
                lastY = event.y
            }
        }
        super.onTouchEvent(event)
        return true
    }

    override fun scrollTo(x: Int, y: Int) {
        val realX = min(max(minScroll, x), maxScrollX)
        super.scrollTo(realX, y)
    }

    fun setMatchSource(str: String?) {
        i = 0
        j = 0
        originalText = if (str.isNullOrEmpty()) {
            null
        } else {
            SpannableString(str)
        }
        text = originalText
    }

    fun match(txt: String?) {
        val source = originalText
        if (source.isNullOrEmpty()) {
            return
        }
        if (txt.isNullOrEmpty()) {
            return
        }
        var flag = true
        var s1 = j
        var c1: Char
        var s2 = max(txt.length - k, 0)
        var c2: Char
        while (s1 < source.length) {
            c1 = source[s1]
            if (flag && symbolList.contains(c1)) {
                s1++
            } else if (s2 < txt.length) {
                c2 = txt[s2]
                if (c1 == c2) {
                    s1++
                    s2++
                    flag = true
                } else {
                    s2++
                    flag = false
                }
            } else {
                break
            }
        }
        if (s1 <= j) {
            return
        }
        i = j
        j = s1
        if (maxScrollX > 0) {
            val sx =
                (j.toFloat() / source.length * (measuredWidth + maxScrollX) - measuredWidth / 2f).toInt()
            scrollX = sx
        }
        source.clearSpans()
        if (i > 0) {
            source.setSpan(
                maskInfo.highlightSpan, 0, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (j > i) {
            source.setSpan(
                maskInfo.currentColorSpan, i, j, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            source.setSpan(
                maskInfo.currentShadowSpan, i, j, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        text = source
    }

    class TextMatchMask(resources: Resources) {
        private val highlightColor = Color.parseColor("#9BDEF4")
        private val maskFilter =
            BlurMaskFilter(resources.displayMetrics.density * 4f, BlurMaskFilter.Blur.SOLID)
        val maskWidth = resources.displayMetrics.density * 20f
        val maskPaint: Paint = Paint().apply {
            val lg = LinearGradient(
                0f,
                0f,
                maskWidth,
                0f,
                Color.parseColor("#80000000"),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            setShader(lg)
        }
        val highlightSpan = ForegroundColorSpan(highlightColor)
        val currentColorSpan = ForegroundColorSpan(Color.WHITE)
        val currentShadowSpan = MaskFilterSpan(maskFilter)
    }

}



