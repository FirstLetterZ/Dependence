package com.zpf.aaa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.zpf.aaa.midea.IMediaEncodeListener
import com.zpf.aaa.midea.IMediaSynthListener
import com.zpf.aaa.midea.TestSynth
import com.zpf.aaa.midea.TestSynthBuilder
import com.zpf.aaa.utils.New
import com.zpf.aaa.utils.Util
import com.zpf.file.FileIOUtil
import com.zpf.file.FileSaveUtil
import com.zpf.tool.BitmapCachePool
import com.zpf.tool.permission.PermissionManager
import com.zpf.tool.permission.model.PermissionGrantedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author Created by ZPF on 2021/3/30.
 */
class TestActivity : AppCompatActivity() {

    //    private val surfaceView by lazy {
//        findViewById<SurfaceView>(R.id.sv_preview)
//    }
    private val ivPreview by lazy {
        findViewById<ImageView>(R.id.iv_preview)
    }
    private val layoutVideo by lazy {
        findViewById<View>(R.id.layout_video)
    }
    private val tvMsg by lazy {
        findViewById<TextView>(R.id.tv_message)
    }
    private var synth: TestSynth? = null
    private val executor: Executor = Executors.newFixedThreadPool(2)
    private val outFile by lazy {
        File(cacheDir, "Test_" + System.currentTimeMillis() + ".mp4")
    }
    private val rect = Rect()

    //    private var bitmap: Bitmap? = null
    private var bitmapPool: BitmapCachePool? = null

    private val progressListener = object : IMediaSynthListener {
        override fun onProgress(presentationTimeUs: Long, durationUs: Long, completed: Boolean) {
            Log.e("ZPF", "onProgress==>>presentationTimeUs=$presentationTimeUs")
            tvMsg.post {
                tvMsg.text =
                    "current=${presentationTimeUs};durationUs=${durationUs},completed=$completed"
            }
            if (completed) {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(1000L)
                    FileSaveUtil.saveFile(this@TestActivity, outFile, null, null, false)
                }
            }
        }
    }

    private var shouldTakePhoto = true
    private val encoderListener = object : IMediaEncodeListener {
        override fun onEncode(image: Image, presentationTimeUs: Long) {
            Log.e("ZPF", "onEncode==>>presentationTimeUs=$presentationTimeUs")
            val surface = synth?.inputSurface ?: return
            val canvas = surface.lockCanvas(image.cropRect) ?: return
//            val canvas = surface.lockCanvas(rect) ?: return
//            canvas.drawColor(Color.RED)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            val bitmap = Util.covertToBitmap(image)
            canvas.drawBitmap(bitmap, 0f, 0f, null)

//            canvas.getJournal()
//            if (cacheBitmap != null) {
//                Util.compressToBitmap(cacheBitmap, image)
//            if (presentationTimeUs > 2000L && shouldTakePhoto) {
//                shouldTakePhoto = false
//                val bitmap = Util.covertToBitmap(image)
//                val res = FileSaveUtil.saveBitmap(
//                    bitmap,
//                    Bitmap.CompressFormat.JPEG,
//                    File(cacheDir, "frame3.jpg")
//                )
//                Log.e("ZPF", "  FileSaveUtil.saveBitmap==>success=${res}")
//            }
//                    try {
//                        Util.compressToJpeg(File(cacheDir,"frame.jpg").absolutePath,image)
//                        Log.e("ZPF", "FileSaveToJpeg==>success")
//                    } catch (e: Exception) {
//                        Log.e("ZPF", "FileSaveToJpeg==>$e")
//                    }

//                }
//                canvas.drawBitmap(cacheBitmap, 0f, 0f, null)
//            }
            layoutVideo.draw(canvas)
            surface.unlockCanvasAndPost(canvas)

//            Thread.sleep(10L)
//            if (presentationTimeUs > 2000L && shouldTakePhoto && cacheBitmap != null) {
//                shouldTakePhoto = false
//                try {
//                    Util.compressToJpeg(File(cacheDir, "frame.jpg").absolutePath, image)
//                    Log.e("ZPF", "FileSaveToJpeg==>success")
//                } catch (e: Exception) {
//                    Log.e("ZPF", "FileSaveToJpeg==>$e")
//                }
//
//            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val albumLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val uri = it.data?.data
                    printVideoInfo(uri)
                }
            }
        findViewById<View>(R.id.btn_select).setOnClickListener {
            val permissions = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                emptyArray()
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            PermissionManager.get().checkPermission(this, permissions, PermissionGrantedListener {
                val albumIntent = Intent(Intent.ACTION_PICK)
                albumIntent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*"
                )
                albumLauncher.launch(albumIntent)
            })
        }

//        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                Log.e("ZPF", "====surfaceCreated====")
////                synth?.setOutputSurface(holder.surface, null)
//            }
//
//            override fun surfaceChanged(
//                holder: SurfaceHolder, format: Int, width: Int, height: Int
//            ) {
//                Log.e("ZPF", "====surfaceChanged====")
////                synth?.setOutputSurface(holder.surface, null)
//
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//            }
//        })
    }


    private fun printVideoInfo(uri: Uri?) {
        if (uri == null) {
            return
        }
        val file = File(cacheDir, "test.mp4")
//        FileIOUtil.writeToFile(contentResolver.openInputStream(uri), file, false)
//        val testNew = New()
//        testNew.setSaveFrames(File(cacheDir, "test_images").absolutePath, 3)
//        testNew.videoDecode(file.absolutePath)

//        val builder = TestSynthBuilder(this, uri, null)
        val builder = TestSynthBuilder(file.absolutePath, null)
        val outWidth = builder.mediaInfo.getTrueWidth()
        val outHeight = builder.mediaInfo.getTrueHeight()

        if (outWidth > 0 && outHeight > 0) {
            val scale = outWidth.toFloat() / layoutVideo.measuredWidth
            layoutVideo.scaleX = scale
            layoutVideo.scaleY = scale
            layoutVideo.updateLayoutParams {
                this.width = outWidth
                this.height = outHeight
            }
        } else {
            return
        }

        val realSynth: TestSynth = builder.build(outFile.absolutePath) as TestSynth
        synth?.release()
        realSynth.progressListener = progressListener
        realSynth.outputListener = encoderListener
//        realSynth.setOutputSurface(surfaceView.holder.surface, null)
        synth = realSynth
        val bitmap = realSynth.retriever.getFrameAtTime(1000000L)
        ivPreview.setImageBitmap(bitmap)
        val cacheBitmap = bitmapPool
        if (cacheBitmap == null || !cacheBitmap.checkSize(outWidth, outHeight)) {
            bitmapPool = BitmapCachePool(outWidth, outHeight)
        }
        rect.set(0, 0, outWidth, outHeight)
        lifecycleScope.launch {
            delay(1000L)
            Log.e("ZPF", "====start====")
            realSynth.start(executor)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        synth?.release()
        synth = null
    }
}
