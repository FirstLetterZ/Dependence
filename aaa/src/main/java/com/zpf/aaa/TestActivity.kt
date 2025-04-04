package com.zpf.aaa

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaCodec
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.withSave
import androidx.lifecycle.lifecycleScope
import com.zpf.aaa.databinding.ActivityTestBinding
import com.zpf.aaa.videorope.VideoCoverBuilder2
import com.zpf.aaa.videorope.VideoFormatBuilder
import com.zpf.file.FileSaveUtil
import com.zpf.media.synth.i.IMediaSynth
import com.zpf.media.synth.i.ISynthOutputListener
import com.zpf.media.synth.i.ISynthStatusListener
import com.zpf.media.synth.i.ISynthSurfaceManager
import com.zpf.media.synth.model.MediaSynthStatus
import com.zpf.media.synth.util.InputSurface
import com.zpf.media.synth.util.OutputSurface
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
    private val binding: ActivityTestBinding by lazy {
        ActivityTestBinding.inflate(layoutInflater, null, false)
    }
    private var startTime = 0L
    private var mOutputSurface: OutputSurface? = null
    private var mInputSurface: InputSurface? = null
    private var synth: IMediaSynth? = null
    private val outFile by lazy {
        File(cacheDir, "Test_" + System.currentTimeMillis() + ".mp4")
    }

    private val progressListener = object : ISynthStatusListener {
        override fun onProgress(presentationTimeUs: Long, durationUs: Long, completed: Boolean) {
            Log.i("ZPF", "onProgress==>>presentationTimeUs=$presentationTimeUs")
            val showText =
                "current=${presentationTimeUs}\ndurationUs=${durationUs}\ncompleted=$completed"
            lifecycleScope.launch(Dispatchers.Main) {
                binding.tvMessage.text = showText
                if (completed) {
                    FileSaveUtil.saveFile(this@TestActivity, outFile, null, null, false)

                }
            }
        }

        override fun onStatusChanged(oldCode: Int, newCode: Int) {
//            Log.w("ZPF", "onStatusChanged==>>oldCode=$oldCode;newCode=$newCode")
            if (newCode == MediaSynthStatus.COMPLETE) {
                Log.w("ZPF", "cost time=${System.currentTimeMillis() - startTime}；path=${outFile.absolutePath}")
                val builder = VideoFormatBuilder("")
                builder.addInput(outFile.absolutePath, null)
                builder.build()
            }
        }
    }
    private val synthSurfaceManager = object : ISynthSurfaceManager {
        override fun onDecoderInputSurfaceCreated(partIndex: Int, surface: Surface) {
        }

        override fun getDecoderOutputSurface(partIndex: Int): Surface? {
            if (partIndex == 1) {
                val cacheSurface = mOutputSurface
                if (cacheSurface?.surface?.isValid == true) {
                    return cacheSurface.surface
                }
                val oSurface = OutputSurface()
                mOutputSurface = oSurface
                return oSurface.surface
            }
            return null
        }

        override fun onEncoderInputSurfaceCreated(partIndex: Int, surface: Surface) {
            if (partIndex == 1) {
                val iSurface = InputSurface(surface)
                iSurface.makeCurrent()
                mInputSurface = iSurface
            }
        }

        override fun getEncoderOutputSurface(partIndex: Int): Surface? {
            return null
        }
    }
    private val synthOutputListener = object : ISynthOutputListener {
        override fun onDecoderOutput(
            partIndex: Int,
            bufferIndex: Int,
            decoder: MediaCodec,
            bufferInfo: MediaCodec.BufferInfo,
            encoder: MediaCodec?
        ) {

            if (partIndex == 1) {
                val decoderOutputSurface = mOutputSurface ?: return
                try {
                    decoderOutputSurface.awaitNewImage()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }
                val startTime = synth?.getPartStartTime(partIndex) ?: 0L
                val timeUs = bufferInfo.presentationTimeUs
                val realTimeUs = (startTime + timeUs) * 1000
                bufferInfo.presentationTimeUs = realTimeUs
                decoderOutputSurface.drawImage(false, null)
                val encoderInputSurface = mInputSurface ?: return
                encoderInputSurface.setPresentationTime(realTimeUs)
                encoderInputSurface.swapBuffers()
            }
        }

        override fun onEncoderOutput(
            partIndex: Int,
            bufferIndex: Int,
            encoder: MediaCodec,
            bufferInfo: MediaCodec.BufferInfo
        ) {
        }
    }
    private var onlyPrint = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val albumLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val uri = it.data?.data
                    if (onlyPrint) {
                        printFormat(uri)
                    } else {
                        printVideoInfo(uri)
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
        binding.run {
            btnSelect.setOnClickListener {
                onlyPrint = false
                pick.run()
            }
            btnPause.text = "print"
            btnPause.setOnClickListener {
                onlyPrint = true
                pick.run()
            }
        }
    }

    private fun printFormat(uri: Uri?) {
        if (uri == null) {
            return
        }
        val builder = VideoFormatBuilder("")
        builder.addInput(this, uri, null)
        builder.build()
    }

    private fun printVideoInfo(uri: Uri?) {
        if (uri == null) {
            return
        }
        synth?.stop()
        mInputSurface?.release()
        mOutputSurface?.release()
        val synthBuilder = VideoCoverBuilder2(outFile.absolutePath, 3)
        synthBuilder.addInput(this, uri, null)
        val realSynth = synthBuilder.build()
        if (realSynth == null) {
            Log.w("ZPF", "build fail")
            return
        }
        realSynth.addStatusListener(progressListener)
//        realSynth.setSynthSurfaceManager(synthSurfaceManager)
//        realSynth.setTackOutputListener(MediaSynthTrackId.VIDEO, synthOutputListener)
        synth = realSynth
        realSynth.start()
        startTime = System.currentTimeMillis()
        lifecycleScope.launch(Dispatchers.IO) {
            var inputSurface = realSynth.getEncoderInputSurface()
            var tryTime = 0
            while (inputSurface == null) {
                if (tryTime > 10) {
                    throw RuntimeException("can not create encoder input surface")
                }
                delay(20L)
                inputSurface = realSynth.getEncoderInputSurface()
                tryTime++
            }
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(this@TestActivity, uri)
            }
            val bitmap = retriever.frameAtTime
            inputSurface.let { surface ->
                val canvas = surface.lockCanvas(null)
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
//                val basicInfo = realSynth.getOutputBasicInfo()

//                Log.w("ZPF", "getOutputBasicInfo=${basicInfo}")
//                Log.w("ZPF", "canvas=${canvas.width},${canvas.height}")
                canvas.withSave {
//                    if( canvas.width!=bitmap.height){
//                                            canvas.rotate(-90f, 0f, 0f)
//                    canvas.translate(-canvas.height.toFloat(), 0f)
//                    }

//                    canvas.rotate(-90f, 0f, 0f)
//                    canvas.translate(-canvas.height.toFloat(), 0f)
                    bitmap?.let {
                        canvas.drawBitmap(it, 0f, 0f, null)
                    }
                    val scale = canvas.width.toFloat() / binding.layoutFront.width
                    scale(scale, scale)
                    binding.layoutFront.draw(this)
                }
                surface.unlockCanvasAndPost(canvas)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        synth?.stop()
        synth = null
    }

}
