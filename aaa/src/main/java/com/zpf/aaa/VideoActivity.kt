package com.zpf.aaa

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val page: ViewPager2 = findViewById(R.id.vp_page)
        page.offscreenPageLimit = 1
        page.orientation = ViewPager2.ORIENTATION_VERTICAL
        page.adapter = VideoPageAdapter()
//        page.setCurrentItem(Int.MAX_VALUE / 2, false)
        page.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    page.stopNestedScroll()
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    page.stopNestedScroll()
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

        })
        lifecycleScope.launchWhenResumed {
            var i = 0
            while (i < 9) {
                delay(1000L)
                Log.e(
                    "ZPF",
                    "h-1=${page.canScrollHorizontally(-1)};" +
                            "h+1=${page.canScrollHorizontally(1)};" +
                            "v-1=${page.canScrollVertically(-1)};" +
                            "v+1=${page.canScrollVertically(1)};"
                )
                i++
            }
        }
    }
}