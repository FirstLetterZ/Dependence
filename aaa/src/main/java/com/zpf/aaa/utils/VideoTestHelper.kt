package com.zpf.aaa.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File

class VideoTestHelper(val activity: ComponentActivity) {
    val inputFile= File(activity.cacheDir,"Video_test_input.mp4")
    val outputFile= File(activity.cacheDir,"Video_test_out_put.mp4")
    private val albumLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data
                printVideoInfo(uri)
            }
        }


    fun pick() {
        val albumIntent = Intent(Intent.ACTION_PICK)
        albumIntent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*"
        )
        albumLauncher.launch(albumIntent)
    }

    private fun printVideoInfo(uri: Uri?) {
        if (uri == null) {
            return
        }



    }
}