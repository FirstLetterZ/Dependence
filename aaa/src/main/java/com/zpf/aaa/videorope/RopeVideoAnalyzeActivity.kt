package com.zpf.aaa.videorope

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.util.Log
import android.util.Size
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.zpf.media.synth.i.IMediaSynth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class RopeVideoAnalyzeActivity : BaseVideoAnalyzeActivity() {

    protected val viewScale by lazy { resources.displayMetrics.density * 0.5f }

    //    protected var shouldShowQuiteAlert = false
//    private var previewBitmap: Bitmap? = null
    override val scene: String
        get() = "jump_rope_count_video"
    private var previewSize = Size(0, 0)

//    override fun findViews() {
//        super.findViews()
//        val videoSynth = mediaSynth ?: return
//        val bitmap = retriever.getFrameAtTime(500000L)
//        previewBitmap = bitmap
//        val basicInfo = videoSynth.getOutputBasicInfo()
//        resultResolver.detectView = binding.drawDetectView
//    }

//    protected fun calcPreviewSize(
//        measuredWidth: Int, measuredHeight: Int, mediaInfo: MediaOutputBasicInfo
//    ): Size {
//        val imageWidth = mediaInfo.width
//        val imageHeight = mediaInfo.height
//        if (imageWidth <= 0 || imageHeight <= 0 || measuredWidth <= 0 || measuredHeight <= 0) {
//            return Size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        }
//        val imageRatio: Float = imageWidth.toFloat() / imageHeight.toFloat()
//        val viewRatio: Float = measuredWidth.toFloat() / measuredHeight.toFloat()
//        return if (imageRatio > viewRatio) {
//            Size(measuredWidth, (measuredWidth / imageRatio).toInt())
//        } else {
//            Size((measuredHeight * imageRatio).toInt(), measuredHeight)
//        }
//    }

    override fun drawUI(stageIndex: Int, canvas: Canvas) {
        if (stageIndex == 0) {
            layoutFront.draw(canvas)
        } else if (stageIndex == 1) {
            tvMsg.draw(canvas)
        }
    }

    override fun onProgressChange(
        presentationTimeUs: Long, durationUs: Long, index: Int, synth: IMediaSynth
    ) {
        super.onProgressChange(presentationTimeUs, durationUs, index, synth)
        val p = presentationTimeUs * 100 / durationUs
        Log.e("ZPF","onProgressChange==>index=$index;p=$p")
//        if (index == 0) {
//            binding.pbProgress.progress = p.toInt()
//        } else {
//            binding.pbProgress.secondaryProgress = p.toInt()
//        }
    }

    override suspend fun onError(stageIndex: Int, code: Int) {
        val dialog = AlertDialog.Builder(this).setMessage("解析意外失败，请稍后重试")
            .setNegativeButton("确定") { dialog, _ ->
                finish()
                dialog.dismiss()
            }
        lifecycleScope.launch(Dispatchers.Main) {
            dialog.show()
        }
    }

    override suspend fun onStageStart(stageIndex: Int, synth: IMediaSynth) {
        if (stageIndex == 1) {
            var inputSurface = synth.getEncoderInputSurface()
            var tryTime = 0
            while (inputSurface == null) {
                if (tryTime > 10) {
                    return
                }
                delay(20L)
                inputSurface = synth.getEncoderInputSurface()
                tryTime++
            }
            inputSurface.let { surface ->
                val canvas = surface.lockCanvas(null)
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                val previewBitmap = retriever.getFrameAtTime(500000L)

                previewBitmap?.let {
                    if (it.width != canvas.width || it.height != canvas.height) {
                        val matrix = Matrix()
                        matrix.setScale(
                            canvas.width.toFloat() / it.width,
                            canvas.height.toFloat() / it.height
                        )
                        canvas.drawBitmap(it, matrix, null)
                    } else {
                        canvas.drawBitmap(it, 0f, 0f, null)
                    }
//                    previewBitmap = null
                }
                canvas.save()
                val scale = 1f / viewScale
                canvas.scale(scale, scale)
                drawUI(1, canvas)
                canvas.restore()
                surface.unlockCanvasAndPost(canvas)
            }
        }
    }

    override suspend fun onStageEnd(stageIndex: Int, synth: IMediaSynth) {
        Log.e("ZPF", "onStageEnd==>stageIndex=$stageIndex")
    }

//    protected fun quite() {
//        if (shouldShowQuiteAlert) {
//            AlertDialog.Builder(this).setMessage("是否返回并放弃处理的视频").setPositiveButton(
//                "等一等"
//            ) { dialog: DialogInterface, _: Int ->
//                dialog.dismiss()
//            }.setNegativeButton(
//                "返回"
//            ) { dialog: DialogInterface, _: Int ->
//                dialog.dismiss()
//                finish()
//            }.show()
//        } else {
//            finish()
//        }
//    }
}