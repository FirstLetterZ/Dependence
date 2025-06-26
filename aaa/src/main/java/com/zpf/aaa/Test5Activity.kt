package com.zpf.aaa

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zpf.aaa.utils.DialogQueueManager
import kotlinx.coroutines.delay

class Test5Activity : AppCompatActivity() {

    private val manager by lazy { DialogQueueManager() }
    private var checkShowDialog = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test5)
        lifecycleScope.launchWhenResumed {
            manager.postDelay({
                AlertDialog.Builder(this@Test5Activity)
                    .setMessage("11111111")
                    .setPositiveButton("确定") { _: DialogInterface?, _: Int -> checkShowDialog = true }
                    .show()
                checkShowDialog = false
            }, 99, 8000L)
            manager.post {
                AlertDialog.Builder(this@Test5Activity)
                    .setMessage("2222")
                    .setPositiveButton("确定") { _: DialogInterface?, _: Int -> checkShowDialog = true }
                    .show()
                checkShowDialog = false
            }
            manager.post({
                AlertDialog.Builder(this@Test5Activity)
                    .setMessage("3333")
                    .setPositiveButton("确定") { _: DialogInterface?, _: Int -> checkShowDialog = true}
                    .show()
                checkShowDialog = false
            }, 9)
            delay(1000L)
            manager.next()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkShowDialog) {
            manager.next()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        manager.quite()
    }

    private fun checkShowDialog() {

    }
}