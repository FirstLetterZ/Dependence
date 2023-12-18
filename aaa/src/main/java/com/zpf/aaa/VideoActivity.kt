package com.zpf.aaa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val page: ViewPager2 = findViewById(R.id.vp_page)
        page.offscreenPageLimit = 1
        page.orientation = ViewPager2.ORIENTATION_VERTICAL
        page.adapter = VideoPageAdapter()
    }
}