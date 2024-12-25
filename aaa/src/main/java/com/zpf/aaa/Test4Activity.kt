package com.zpf.aaa

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zpf.aaa.banner.ContentAdapter
import com.zpf.aaa.banner.IndicatorAdapter
import com.zpf.tool.func.TwoDimensionalFunction
import java.util.UUID

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

        val f1 = TwoDimensionalFunction(0f, -1f, -1f, 0f, 0f, 10f)
        val f2 = TwoDimensionalFunction(0f, 1f, -1f, 0f, 0f, -20f)
        val accuracy = 0.001f
        val cross = f1.crossPoint(f2)
        Log.w("ZPF", "crossPoint==> ${cross?.size ?: 0}")
        cross?.forEach {
            Log.w(
                "ZPF", "checkResult==>x=${it.x};y=${it.y};" +
                        "1=${f1.checkResult(it.x, it.y, accuracy)};" +
                        "2=${f2.checkResult(it.x, it.y, accuracy)}"
            )
        }
    }

    private fun updateContent() {
        val list = ArrayList<String>()
        for (i in 0..5) {
            list.add("${i + 1}\n" + UUID.randomUUID().toString())
        }
        Log.e("ZPF", "== updateContent ==")
        contentAdapter.submitList(list)
    }

}