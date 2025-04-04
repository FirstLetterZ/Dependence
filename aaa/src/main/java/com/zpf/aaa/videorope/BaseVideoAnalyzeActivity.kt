package com.zpf.aaa.videorope

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.media.MediaCodec
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.opengl.GLES20
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.zpf.aaa.R
import com.zpf.media.synth.i.IMediaSynth
import com.zpf.media.synth.i.ISynthOutputListener
import com.zpf.media.synth.i.ISynthStatusListener
import com.zpf.media.synth.i.ISynthSurfaceManager
import com.zpf.media.synth.model.MediaSynthStatus
import com.zpf.media.synth.model.MediaSynthTrackId
import com.zpf.media.synth.util.InputSurface
import com.zpf.media.synth.util.OutputSurface
import com.zpf.tool.permission.PermissionManager
import com.zpf.tool.permission.model.PermissionGrantedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

abstract class BaseVideoAnalyzeActivity : AppCompatActivity() {

    protected val tempFile: File by lazy {
        val dir = File(cacheDir, "video_count_temp")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "video.mp4")
        if (file.exists()) {
            file.delete()
        }
        file
    }
    protected val resultFile: File by lazy {
        val dir = File(cacheDir, "video_count_result")
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "video.mp4")
        if (file.exists()) {
            file.delete()
        }
        file
    }
    protected val executor: ExecutorService = Executors.newSingleThreadExecutor()
    protected var deleteOutputFile = true
    protected var mediaSynth: IMediaSynth? = null
    protected var inputSurface: InputSurface? = null
    protected var outputSurface: OutputSurface? = null
    protected abstract val scene: String
    protected var currentStateIndex = 0
    protected val detecterStartTime = AtomicLong(0L)
    protected val detecterFinishTime = AtomicLong(0L)
    protected val retriever by lazy { MediaMetadataRetriever() }
    protected val layoutVideo by lazy {
        findViewById<View>(R.id.layout_video)
    }
    protected val layoutFront by lazy {
        findViewById<View>(R.id.layout_front)
    }
    protected val tvMsg by lazy {
        findViewById<TextView>(R.id.tv_message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val albumLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val uri = it.data?.data
                    if (uri != null) {
                        startSynth(uri)
                    }
                }
            }
        val pick = Runnable {
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
        findViewById<View>(R.id.btn_select).setOnClickListener {
//            testCompress()
            pick.run()
        }
        findViewById<View>(R.id.btn_pause).setOnClickListener {
            mediaSynth?.let {
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

    @CallSuper
    protected open fun onProgressChange(
        presentationTimeUs: Long, durationUs: Long, index: Int, synth: IMediaSynth
    ) {
    }

    protected suspend fun startNextStage(nextIndex: Int) {
        if (nextIndex > 1) {
            return
        }
        val builder = VideoCoverBuilder(resultFile.absolutePath)
        builder.addInput(tempFile.absolutePath, null)
        val newSynth = builder.build()
        if (newSynth == null) {
            withContext(Dispatchers.Main) {
                Log.e("ZPF", "文件异常")
            }
            return
        }
        newSynth.addStatusListener(object : ISynthStatusListener {
            override fun onProgress(
                presentationTimeUs: Long, durationUs: Long, completed: Boolean
            ) {
                onProgressChange(
                    presentationTimeUs, durationUs, nextIndex, newSynth
                )
            }

            override fun onStatusChanged(oldCode: Int, newCode: Int) {
                lifecycleScope.launch {
                    if (newCode == MediaSynthStatus.COMPLETE) {
                        onStageEnd(nextIndex, newSynth)
                    } else if (newCode < 0) {
                        onError(nextIndex, newCode)
                    }
                }
            }
        })
        newSynth.start()
        onStageStart(nextIndex, newSynth)
    }


    protected abstract suspend fun onError(stageIndex: Int, code: Int)
    protected abstract suspend fun onStageStart(stageIndex: Int, synth: IMediaSynth)
    protected abstract suspend fun onStageEnd(stageIndex: Int, synth: IMediaSynth)
    protected abstract fun drawUI(stageIndex: Int, canvas: Canvas)

    override fun onStart() {
        super.onStart()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mediaSynth?.let {
            if (it.status() == MediaSynthStatus.CREATE) {
                it.start()
                lifecycleScope.launch {
                    onStageStart(0, it)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
//        mediaSynth?.pause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSynth?.stop()
        if (deleteOutputFile) {
            if (resultFile.delete()) {
                resultFile.parentFile?.delete()
            }
        }
        tempFile.delete()
        inputSurface?.release()
        outputSurface?.release()
        inputSurface = null
        outputSurface = null
    }

    private fun detecterPoints(width: Int, height: Int, time: Long, pixelBuffer: ByteBuffer) {
        val bytes: ByteArray
        var cache = bufferList.poll()
        if (cache == null) {
            if (bufferCacheSize.decrementAndGet() >= 0) {
                bytes = ByteArray(width * height * 4)
            } else {
                var n = 10
                cache = bufferList.poll()
                while (cache == null && n > 0) {
                    Thread.sleep(10L)
                    n--
                    cache = bufferList.poll()
                }
                if (cache == null) {
                    return
                }
                bytes = cache
            }
        } else {
            bytes = cache
        }
        val startTime = System.currentTimeMillis()
        detecterStartTime.set(startTime)
        pixelBuffer.rewind()
        GLES20.glReadPixels(
            0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer
        )
        pixelBuffer.rewind()
//        MediaSynthUtil.rotateRgba180Degrees(
//            pixelBuffer.array(), width, height, bytes
//        )
//        val dt = System.currentTimeMillis() - lastTime
//        if (dt > 30000L) {
//            lastTime = System.currentTimeMillis()
//        } else if (dt > 8000L) {
//            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//            bitmap.copyPixelsFromBuffer(pixelBuffer)
//            lifecycleScope.launch {
//                val res = saveImage(bitmap)
//            }
//            lastTime = System.currentTimeMillis()
//        }
//        executor.submit {
//            if (skipNext.get()) {
//                skipNext.set(false)
//            } else if (System.currentTimeMillis() - startTime < 32L) {
//                resultResolver.detect(
//                    bytes, width, height, time, false, 0f, MNNImageProcess.Format.RGBA
//                )
//                if (System.currentTimeMillis() - startTime > 32L) {
//                    skipNext.set(true)
//                }
//            }
//            detecterFinishTime.set(System.currentTimeMillis())
//            bufferList.add(bytes)
//        }
    }

    protected fun onFinishDetect() {
        bufferList.clear()
        bufferCacheSize.set(8)
        skipNext.set(false)
        executor.shutdown()
        inputSurface?.release()
        outputSurface?.release()
        inputSurface = null
        outputSurface = null
    }

    private val bufferList = LinkedBlockingQueue<ByteArray>(8)
    private val bufferCacheSize = AtomicInteger(8)
    private val skipNext = AtomicBoolean(false)

    private fun startSynth(fileUri: Uri) {
        val builder = VideoSynthBuilder(tempFile.absolutePath)
        builder.addInput(this, fileUri, null)
        val synth = builder.build() ?: return
        mediaSynth = synth
        retriever.setDataSource(this, fileUri)
//        val outputInfo = synth.getOutputBasicInfo()
//        val pixelBuffer: ByteBuffer = ByteBuffer.allocate(outputInfo.width * outputInfo.height * 4)
        synth.setTackOutputListener(MediaSynthTrackId.VIDEO, object : ISynthOutputListener {
            override fun onDecoderOutput(
                partIndex: Int,
                bufferIndex: Int,
                decoder: MediaCodec,
                bufferInfo: MediaCodec.BufferInfo,
                encoder: MediaCodec?
            ) {
                val decoderOutputSurface = outputSurface ?: return
                try {
                    decoderOutputSurface.awaitNewImage()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
                val timeUs = bufferInfo.presentationTimeUs
                decoderOutputSurface.drawImage(false, null)
//                detecterPoints(outputInfo.width, outputInfo.height, timeUs / 1000L, pixelBuffer)
                val encoderInputSurface = inputSurface ?: return
                encoderInputSurface.setPresentationTime(timeUs * 1000)
                encoderInputSurface.swapBuffers()
            }

            override fun onEncoderOutput(
                partIndex: Int,
                bufferIndex: Int,
                encoder: MediaCodec,
                bufferInfo: MediaCodec.BufferInfo
            ) {
            }
        })
        synth.setSynthSurfaceManager(object : ISynthSurfaceManager {
            override fun onDecoderInputSurfaceCreated(partIndex: Int, surface: Surface) {}
            override fun getDecoderOutputSurface(partIndex: Int): Surface? {
                val cacheSurface = outputSurface
                if (cacheSurface?.surface?.isValid == true) {
                    return cacheSurface.surface
                }
                val oSurface = OutputSurface()
                outputSurface = oSurface
                return oSurface.surface
            }

            override fun onEncoderInputSurfaceCreated(partIndex: Int, surface: Surface) {
                val iSurface = InputSurface(surface)
                iSurface.makeCurrent()
                inputSurface = iSurface
            }

            override fun getEncoderOutputSurface(partIndex: Int): Surface? {
                return null
            }
        })
        val checkInterval = 40L
        synth.addStatusListener(object : ISynthStatusListener {
            override fun onProgress(
                presentationTimeUs: Long, durationUs: Long, completed: Boolean
            ) {
                onProgressChange(presentationTimeUs, durationUs, 0, synth)
            }

            override fun onStatusChanged(oldCode: Int, newCode: Int) {
                if (newCode == MediaSynthStatus.COMPLETE) {
                    lifecycleScope.launch {
                        var n = 20
                        while (n > 0) {
                            n--
                            val current = System.currentTimeMillis()
                            val end = detecterFinishTime.get()
                            val start = detecterStartTime.get()
                            if (current - end > checkInterval && current - start > checkInterval) {
                                onFinishDetect()
                                break
                            }
                            delay(checkInterval)
                        }
                        onStageEnd(0, synth)
                        currentStateIndex = 1
                        startNextStage(1)
                    }
                } else if (newCode < 0) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        onError(0, newCode)
                    }
                }
            }
        })
        synth.start()
    }
}