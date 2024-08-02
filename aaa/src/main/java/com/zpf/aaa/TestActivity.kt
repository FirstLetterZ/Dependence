package com.zpf.aaa

import android.app.Activity
import android.content.Intent
import android.media.MediaCodec
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import com.zpf.aaa.synth.IMediaSynth
import com.zpf.aaa.synth.ISynthInputSurfaceListener
import com.zpf.aaa.synth.ISynthOutputListener
import com.zpf.aaa.synth.ISynthStatusListener
import com.zpf.aaa.synth.MediaSynthStatus
import com.zpf.aaa.tst.TestSyncSynthBuilder
import com.zpf.aaa.utils.Util
import com.zpf.aaa.videocompressor.InputSurface
import com.zpf.aaa.videocompressor.OutputSurface
import com.zpf.aaa.videocompressor.VideoCompress
import com.zpf.file.FileSaveUtil
import com.zpf.tool.permission.PermissionManager
import com.zpf.tool.permission.model.PermissionGrantedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

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
    private val synthBuilder by lazy {
        TestSyncSynthBuilder(outFile.absolutePath)
    }
    private var inputSurface: InputSurface? = null
    private var outputSurface: OutputSurface? = null
    private var readBuffer: ByteBuffer? = null

    private val surfaceListener = object : ISynthInputSurfaceListener {

        override fun onSurfaceCreated(surface: Surface) {
            inputSurface?.release()
            outputSurface?.release()
//            val mediaInfo = synth?.getInputInfo(0)?.videoInputMediaInfo ?: return
            val iSurface = InputSurface(surface)
            iSurface.makeCurrent()
//            val oSurface = OutputSurface(mediaInfo.width, mediaInfo.height, mediaInfo.rotation)
            val oSurface = OutputSurface()
            inputSurface = iSurface
            outputSurface = oSurface
            synth?.setDecoderOutputSurface(oSurface.surface)
        }
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
            index: Int,
            decoder: MediaCodec,
            bufferInfo: MediaCodec.BufferInfo,
            encoder: MediaCodec?
        ) {
            Log.e("ZPF", "onDecoderOutput==>>presentationTimeUs=${bufferInfo.presentationTimeUs}")
//            val surface = synth?.getEncoderInputSurface() ?: return
//            val image = decoder.getOutputImage(index) ?: return
//            val rect = Rect(
//                0, 0, mediaInfo.getTrueWidth(), mediaInfo.getTrueHeight()
//            )
//            val canvas = surface.lockCanvas(rect) ?: return
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
//            val yuvImage = Util.covert2YuvImage(image, mediaInfo.rotation)
//            val bitmap = Util.covert2Bitmap(yuvImage)
//            canvas.drawBitmap(bitmap, 0f, 0f, null)
//            layoutFront.draw(canvas)
//            surface.unlockCanvasAndPost(canvas)
            val decoderOutputSurface = outputSurface ?: return
            try {
                decoderOutputSurface.awaitNewImage()
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
            layoutFront.buildDrawingCache()
            decoderOutputSurface.drawImage(false, layoutFront.drawingCache)
            Log.e("ZPF", "readImageBytes===>1111")
            readImageBytes()
            val encoderInputSurface = inputSurface ?: return
            encoderInputSurface.setPresentationTime(bufferInfo.presentationTimeUs * 1000)
            encoderInputSurface.swapBuffers()
            Log.e("ZPF", "readImageBytes===>2222")
            readImageBytes()
        }

        override fun onEncoderOutput(
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
//            testCompress()
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
        layoutFront.isDrawingCacheEnabled = true
//        Log.e("ZPF", "${drawBitmap.width};${drawBitmap.height}")
    }

    private fun testCompress() {
        val file = File(cacheDir, "test.mp4")
        VideoCompress.compressVideoMedium(
            file.absolutePath,
            File(cacheDir, "test_compress.mp4").absolutePath,
            object :
                VideoCompress.CompressListener {
                override fun onStart() {
                    Log.e("ZPF", "===onStart===")
                }

                override fun onSuccess() {
                    Log.e("ZPF", "===onSuccess===")
                }

                override fun onFail() {
                    Log.e("ZPF", "===onFail===")
                }

                override fun onProgress(percent: Float) {
                    Log.e("ZPF", "===onProgress===")

                }
            })
        return
    }

    private fun printVideoInfo(uri: Uri?) {
        if (uri == null) {
            return
        }

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

        synthBuilder.addInput(this, uri, null)
        val realSynth = synthBuilder.build()
        if (realSynth == null) {
            Log.w("ZPF", "build fail")
            return
        }
        val info = realSynth.getOutputBasicInfo()
        val outWidth = info.width
        val outHeight = info.height
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

//        val realSynth = SyncMediaSynth(
//            listOf(input),
//            MediaSynthOutput(outFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
//        )

        synth?.stop()
        realSynth.addStatusListener(progressListener)
        realSynth.setVideoListener(encoderListener)
        realSynth.setEncoderInputSurfaceChangedListener(surfaceListener)
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
        inputSurface?.release()
        outputSurface?.release()
        inputSurface = null
        outputSurface = null
    }

    private fun readImageBytes() {
        val outputInfo = synth?.getOutputBasicInfo() ?: return
        var byteCache = readBuffer
        val targetCapacity = outputInfo.width * outputInfo.height * 4
        if (byteCache == null || byteCache.capacity() < targetCapacity) {
            byteCache = ByteBuffer.allocateDirect(outputInfo.width * outputInfo.height * 4)
            byteCache.order(ByteOrder.LITTLE_ENDIAN)
            readBuffer = byteCache
            Util.getFrame(byteCache, outputInfo.width, outputInfo.height)
        } else {
            Util.getFrame(byteCache, outputInfo.width, outputInfo.height)
        }
        Log.e(
            "ZPF",
            "readImageBytes===>position=${byteCache?.position()};mark=${byteCache?.mark()}}"
        )
    }

}
