package com.zpf.aaa

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zpf.aaa.banner.ContentAdapter
import com.zpf.aaa.banner.IndicatorAdapter
import com.zpf.tool.animation.ViewAnimAttribute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Test4Activity : AppCompatActivity() {
    private val contentAdapter by lazy {
        ContentAdapter()
    }
    private val indicatorAdapter by lazy {
        IndicatorAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test4)
//        val rvList = findViewById<RecyclerView>(R.id.rv_list)
//        val rvIndicator = findViewById<RecyclerView>(R.id.rv_indicator)
//        rvList.adapter = contentAdapter
//        rvIndicator.adapter = indicatorAdapter
//        val banner = CarouselBanner(rvList, rvIndicator)
//        banner.addListener(indicatorAdapter)
//        updateContent()
//        lifecycleScope.launch {
//            delay(2000L)
//            rvList.smoothScrollToPosition(3)
//            delay(4000L)
//            updateContent()
//        }
//        banner.start()

        val viewTest = findViewById<View>(R.id.view_test)
        val target1 = findViewById<View>(R.id.space1)
        val target2 = findViewById<View>(R.id.space2)
        val animAttribute = ViewAnimAttribute(viewTest)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000L
        animator.addUpdateListener {
            val fv = it.animatedValue as Float
            animAttribute.run(fv)
        }
        val lis= ViewTreeObserver.OnGlobalLayoutListener {
            animAttribute.setTargetView(target2)
            animator.start()
        }
        target2.viewTreeObserver.addOnGlobalLayoutListener (lis)
        target2.viewTreeObserver.addOnPreDrawListener {
            target2.viewTreeObserver.removeOnGlobalLayoutListener(lis)
            true
        }
        var i = 0
        viewTest.setOnClickListener {
            if (i % 2 == 0) {
                animAttribute.setTargetView(target1)
            } else {
                animAttribute.setTargetView(target2)
            }
            i++
            animator.start()
        }

        lifecycleScope.launch {
            delay(2000L)
            viewTest.requestLayout()
        }
    }


}