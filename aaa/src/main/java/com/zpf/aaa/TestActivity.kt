package com.zpf.aaa

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.media.MediaCodec
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.zpf.aaa.synth.IMediaSynth
import com.zpf.aaa.synth.ISynthOutputListener
import com.zpf.aaa.synth.ISynthStatusListener
import com.zpf.aaa.synth.MediaInfo
import com.zpf.aaa.synth.MediaSynthOutput
import com.zpf.aaa.synth.MediaSynthStatus
import com.zpf.aaa.synth.SyncMediaSynth
import com.zpf.aaa.tst.TestSynthFactory

import com.zpf.aaa.utils.Util
import com.zpf.file.FileSaveUtil
import com.zpf.tool.permission.PermissionManager
import com.zpf.tool.permission.model.PermissionGrantedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

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
    private val layoutFront by lazy {
        findViewById<View>(R.id.layout_front)
    }
    private val tvMsg by lazy {
        findViewById<TextView>(R.id.tv_message)
    }
    private var synth: IMediaSynth? = null
    private val outFile by lazy {
        File(cacheDir, "Test_" + System.currentTimeMillis() + ".mp4")
    }
    private val synthFactory by lazy {
        TestSynthFactory()
    }

    private val progressListener = object : ISynthStatusListener {
        override fun onProgress(presentationTimeUs: Long, durationUs: Long, completed: Boolean) {
            Log.e("ZPF", "onProgress==>>presentationTimeUs=$presentationTimeUs")
            tvMsg.post {
                tvMsg.text =
                    "current=${presentationTimeUs}\ndurationUs=${durationUs}\ncompleted=$completed"
            }
            if (completed) {
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(1000L)
                    FileSaveUtil.saveFile(this@TestActivity, outFile, null, null, false)
                }
            }
        }

        override fun onStatusChanged(oldCode: Int, newCode: Int) {
            Log.e("ZPF", "onStatusChanged==>>oldCode=$oldCode;newCode=$newCode")
        }
    }

    private val encoderListener = object : ISynthOutputListener {

        override fun onDecoderOutput(
            mediaInfo: MediaInfo,
            index: Int,
            decoder: MediaCodec,
            bufferInfo: MediaCodec.BufferInfo,
            encoder: MediaCodec?
        ) {
            Log.e("ZPF", "onDecoderOutput==>>presentationTimeUs=${bufferInfo.presentationTimeUs}")
            val surface = synth?.getEncoderInputSurface() ?: return
            val image = decoder.getOutputImage(index) ?: return
            val rect = Rect(
                0, 0, mediaInfo.getTrueWidth(), mediaInfo.getTrueHeight()
            )
            val canvas = surface.lockCanvas(rect) ?: return
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            val yuvImage = Util.covert2YuvImage(image, mediaInfo.rotation)
            val bitmap = Util.covert2Bitmap(yuvImage)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            layoutFront.draw(canvas)
            surface.unlockCanvasAndPost(canvas)
        }

        override fun onEncoderOutput(
            mediaInfo: MediaInfo,
            index: Int,
            encoder: MediaCodec,
            bufferInfo: MediaCodec.BufferInfo
        ) {

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
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            PermissionManager.get().checkPermission(this, permissions, PermissionGrantedListener {
                val albumIntent = Intent(Intent.ACTION_PICK)
                albumIntent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*"
                )
                albumLauncher.launch(albumIntent)
            })
        }
        findViewById<View>(R.id.btn_pause).setOnClickListener {
            synth?.let {
                val code = it.status()
                when (code) {
                    MediaSynthStatus.START -> {
                        it.pause()

                    }
                    MediaSynthStatus.PAUSE -> {
                        it.start()
                    }
                }
            }
        }
    }

    private fun printVideoInfo(uri: Uri?) {
        if (uri == null) {
            return
        }
//        val file = File(cacheDir, "test.mp4")
//        FileIOUtil.writeToFile(contentResolver.openInputStream(uri), file, false)
//        val testNew = New()
//        testNew.setSaveFrames(File(cacheDir, "test_images").absolutePath, 3)
//        testNew.videoDecode(file.absolutePath)

//        val builder = TestSynthBuilder(this, uri, null)
//        val builder = VideoMuxerTest(file, outFile)
//        videoMuxerTest = builder
//        builder.outputListener = encoderListener
//        builder.progressListener = progressListener

//        val builder = TestSynthBuilder(this, uri, null)
//        val builder = TestSynthBuilder(file.absolutePath, null)

        val input = synthFactory.create(this, uri, null)
        if (input == null) {
            Log.e("ZPF", "input==null")
            return
        }

        val outWidth = input.mediaInfo.getTrueWidth()
        val outHeight = input.mediaInfo.getTrueHeight()

        if (outWidth > 0 && outHeight > 0) {
            val scale1 = layoutVideo.measuredWidth.toFloat() / outWidth.toFloat()
            val scale2 = layoutVideo.measuredHeight.toFloat() / outHeight.toFloat()
            val scale = Math.min(scale1, scale2)
            layoutVideo.scaleX = scale
            layoutVideo.scaleY = scale
            layoutVideo.updateLayoutParams {
                this.width = outWidth
                this.height = outHeight
            }
            Log.e(
                "TEST",
                "measuredWidth=${layoutVideo.measuredWidth};outWidth=${outWidth};measuredHeight=${layoutVideo.measuredHeight};outHeight=${outHeight};scale=${scale}"
            )

//            layoutVideo.scaleX = scale
//            layoutVideo.scaleY = scale
//            layoutVideo.updateLayoutParams {
//                this.width = outWidth
//                this.height = outHeight
//            }
        } else {
            return
        }

        val realSynth = SyncMediaSynth(
            listOf(input),
            MediaSynthOutput(outFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        )

        synth?.stop()
        realSynth.addStatusListener(progressListener)
        realSynth.setVideoListener(encoderListener)
        synth = realSynth
//        val bitmap = realSynth.retriever.getFrameAtTime(1000000L)
//        val bitmap = builder.retriever.getFrameAtTime(1000000L)
//        ivPreview.setImageBitmap(bitmap)
        Log.e("ZPF", "====start====")
        realSynth.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        synth?.stop()
        synth = null
    }

}
