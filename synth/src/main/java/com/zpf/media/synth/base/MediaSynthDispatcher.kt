package com.zpf.media.synth.base

import android.media.MediaCodec
import android.view.Surface
import com.zpf.media.synth.i.ISynthInputPart
import com.zpf.media.synth.i.ISynthOutputListener
import com.zpf.media.synth.i.ISynthSurfaceManager
import com.zpf.media.synth.model.MediaOutputBasicInfo
import com.zpf.media.synth.model.MediaSynthStatus
import com.zpf.media.synth.model.MediaSynthTrackId
import com.zpf.media.synth.model.MediaTrackRecorder
import com.zpf.media.synth.util.MediaSynthLogger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

abstract class MediaSynthDispatcher(
    outputInfo: MediaOutputBasicInfo, protected val inputs: List<ISynthInputPart>
) : MediaSynthStateManager() {
    protected val inputStepTimeOffsetList = ArrayList<Long>()
    protected var lastProgressTime = AtomicLong(0L)
    protected val trackRecordMap = HashMap<String, MediaTrackRecorder>()
    protected val realOutputInfo: MediaOutputBasicInfo
    protected var surfaceManager: ISynthSurfaceManager? = null
    protected var mediaDecoderInputSurface: Surface? = null
    protected var mediaDecoderOutputSurface: Surface? = null
    protected var mediaEncoderInputSurface: Surface? = null
    protected var mediaEncoderOutputSurface: Surface? = null

    init {
        var sum = 0L
        inputStepTimeOffsetList.add(0L)
        inputs.forEach {
            sum += it.getBasicInfo().duration
            inputStepTimeOffsetList.add(sum)
        }
        realOutputInfo = outputInfo.copy(duration = sum)
        MediaSynthTrackId.all().forEach {
            trackRecordMap[it] = MediaTrackRecorder(it)
        }
        MediaSynthLogger.logInfo(
            "MediaSynth==>outputInfo=$realOutputInfo;inputStepTimeOffsetList=${
                inputStepTimeOffsetList.toArray().contentToString()
            }"
        )
    }

    //    override fun reset() {
//        super.reset()
//        trackRecordMap.forEach {
//            it.value.reset()
//        }
//        lastProgressTime.set(0L)
//    }
    override fun onClear() {
        trackRecordMap.forEach { entry ->
            val recorder = entry.value
            val index = recorder.trackPartIndex.get()
            inputs.getOrNull(index)?.getTrackEditor(recorder.trackId)?.stop()
        }
        if (statusCode.get() == MediaSynthStatus.COMPLETE) {
            val totalDuration = getOutputBasicInfo().duration * 1000L
            statusListenerSet.forEach {
                it.onProgress(totalDuration, totalDuration, true)
            }
        }
        releaseSurface()
    }

    override fun getOutputBasicInfo(): MediaOutputBasicInfo = realOutputInfo

    override fun setTackOutputListener(trackId: String, listener: ISynthOutputListener?) {
        trackRecordMap[trackId]?.outputListener = listener
    }

    override fun setSynthSurfaceManager(manager: ISynthSurfaceManager?) {
        surfaceManager = manager
    }

    override fun getDecoderInputSurface(): Surface? {
        return mediaDecoderInputSurface
    }

    override fun getEncoderInputSurface(): Surface? {
        return mediaEncoderInputSurface
    }

    override fun getPartStartTime(partIndex: Int): Long {
        return inputStepTimeOffsetList.getOrNull(partIndex) ?: 0L
    }

    protected fun updateTrackProgress(trackId: String, timeUs: Long) {
        val recorder = trackRecordMap[trackId] ?: return
        recorder.trackProgressTime.set(timeUs)
        var minTrackTime = timeUs
        trackRecordMap.forEach {
            minTrackTime = min(minTrackTime, it.value.trackProgressTime.get())
        }
        val p0 = lastProgressTime.get()
        if (p0 < minTrackTime) {
            lastProgressTime.set(minTrackTime)
            val totalDuration = getOutputBasicInfo().duration * 1000L
            statusListenerSet.forEach {
                it.onProgress(minTrackTime, totalDuration, false)
            }
        }
    }

    protected fun dispatchDecoderOutput(
        partIndex: Int,
        trackId: String,
        index: Int,
        decoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        encoder: MediaCodec?,
    ) {
        trackRecordMap[trackId]?.outputListener?.onDecoderOutput(
            partIndex, index, decoder, bufferInfo, encoder
        )
    }

    protected fun dispatchEncoderOutput(
        partIndex: Int,
        trackId: String,
        index: Int,
        encoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo
    ) {
        trackRecordMap[trackId]?.outputListener?.onEncoderOutput(
            partIndex, index, encoder, bufferInfo
        )
    }

    protected fun isFinished(): Boolean {
        trackRecordMap.forEach {
            if (it.value.trackPartIndex.get() < inputs.size) {
                return false
            }
        }
        return true
    }

    protected fun getCurrentInputConfig(trackId: String): ISynthInputPart? {
        return findInputConfig(trackId, 0)
    }

    protected fun getNextInputConfig(trackId: String): ISynthInputPart? {
        return findInputConfig(trackId, 1)
    }

    protected fun findInputConfig(trackId: String, offsetStartIndex: Int): ISynthInputPart? {
        val recorder = trackRecordMap[trackId] ?: return null
        val indexRecorder = recorder.trackPartIndex
        var inputIndex = indexRecorder.get()
        if (offsetStartIndex != 0) {
            inputIndex += offsetStartIndex
            indexRecorder.set(inputIndex)
        }
        while (inputIndex < inputs.size) {
            val partConfig = inputs.getOrNull(inputIndex)
            val trackEditor = partConfig?.getTrackEditor(trackId)
            if (trackEditor?.isValid() == true) {
                val startTimeUs = getPartStartTime(inputIndex) * 1000L
                recorder.trackProgressTime.set(startTimeUs)
                return partConfig
            }
            inputIndex++
            indexRecorder.set(inputIndex)
        }
        recorder.trackProgressTime.set(Long.MAX_VALUE)
        return null
    }

    protected fun releaseSurface() {
        mediaDecoderInputSurface?.release()
        mediaDecoderOutputSurface?.release()
        mediaEncoderInputSurface?.release()
        mediaEncoderOutputSurface?.release()
        mediaDecoderInputSurface = null
        mediaDecoderOutputSurface = null
        mediaEncoderInputSurface = null
        mediaEncoderOutputSurface = null
    }

}