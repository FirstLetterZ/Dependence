package com.zpf.aaa

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zpf.aaa.utils.SelectorImageView
import com.zpf.aaa.utils.TextRect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStreamReader

class Test4Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test4)
        val image: SelectorImageView = findViewById<SelectorImageView>(R.id.iv_selector)
        val gson = Gson()
        lifecycleScope.launch {
            val inputStream = assets.open("pic_test_image.jpg")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            image.setImageBitmap(bitmap)
            delay(1000L)
            val type = object : TypeToken<List<TextRect>>() {}.type
            val datas: List<TextRect>? =
                gson.fromJson(InputStreamReader(assets.open("text_rez.json")), type)
            image.setTextList(datas)
        }
    }

}