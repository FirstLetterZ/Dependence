package com.zpf.aaa.synth

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.util.Log
import android.view.Surface
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

abstract class BaseMediaSynth(
    protected val inputs: List<MediaSynthInput>, protected val outputWriter: MediaSynthOutput
) : IMediaSynth {
    protected val TAG = "MediaSynth"
    protected val inputStepTimeOffsetList = ArrayList<Long>()
    protected val totalDurationUs: Long
    protected val statusCode = AtomicInteger(0)
    protected val statusListenerSet = HashSet<ISynthStatusListener>()
    protected val workThreadLock = Object()
    protected var lastProgressTime = AtomicLong(0L)
    protected val videoTrackRecorder = MediaTrackRecorder()
    protected val audioTrackRecorder = MediaTrackRecorder()
    protected var mediaDecoderSurfaceListener: ISynthInputSurfaceListener? = null
    protected var mediaDecoderInputSurface: Surface? = null
    protected var mediaDecoderOutputSurface: Surface? = null
    protected var mediaEncoderSurfaceListener: ISynthInputSurfaceListener? = null
    protected var mediaEncoderInputSurface: Surface? = null
    protected var mediaEncoderOutputSurface: Surface? = null
    private val timer = SimpleTimer()
    protected var maxBufferSize = 4 * 1024 * 1024
    private val minBufferSize = 512 * 1024
    private var bufferSize = minBufferSize

    init {
        var sum = 0L
        inputs.forEach {
            inputStepTimeOffsetList.add(sum)
            sum += it.mediaInfo.duration
        }
        totalDurationUs = sum
    }

    override fun status(): Int {
        return statusCode.get()
    }

    override fun start() {
        changeToStatus(MediaSynthStatus.START)
    }

    override fun pause() {
        changeToStatus(MediaSynthStatus.PAUSE)
    }

    override fun stop() {
        changeToStatus(MediaSynthStatus.STOP)
    }

    override fun reset() {
        changeToStatus(MediaSynthStatus.CREATE)
    }

    override fun getDuration(): Long = totalDurationUs * 1000L

    override fun setDecoderInputSurfaceChangedListener(listener: ISynthInputSurfaceListener?) {
        mediaDecoderSurfaceListener = listener
    }

    override fun setDecoderOutputSurface(surface: Surface?) {
        mediaDecoderOutputSurface = surface
    }

    override fun getDecoderInputSurface(): Surface? {
        return mediaDecoderInputSurface
    }

    override fun setEncoderInputSurfaceChangedListener(listener: ISynthInputSurfaceListener?) {
        mediaEncoderSurfaceListener = listener
    }

    override fun setEncoderOutputSurface(surface: Surface?) {
        mediaEncoderOutputSurface = surface
    }

    override fun getEncoderInputSurface(): Surface? {
        return mediaEncoderInputSurface
    }

    override fun getInputInfo(i: Int): MediaSynthInput? {
        return inputs.getOrNull(i)
    }

    override fun setVideoListener(listener: ISynthOutputListener?) {
        videoTrackRecorder.outputListener = listener
    }

    override fun setAudioListener(listener: ISynthOutputListener?) {
        audioTrackRecorder.outputListener = listener
    }

    override fun addStatusListener(listener: ISynthStatusListener) {
        statusListenerSet.add(listener)
    }

    override fun removeStatusListener(listener: ISynthStatusListener) {
        statusListenerSet.remove(listener)
    }

    protected fun notifyWorkThread() {
        synchronized(workThreadLock) {
            workThreadLock.notifyAll()
        }
    }

    protected open fun onStateChanged(oldCode: Int, newCode: Int) {
        when (newCode) {
            MediaSynthStatus.COMPLETE -> {
                timer.stop()
                statusListenerSet.forEach {
                    it.onProgress(getDuration(), getDuration(), true)
                }
                onStop()
            }
            MediaSynthStatus.STOP -> {
                timer.stop()
                onStop()
            }
            MediaSynthStatus.PAUSE -> {
                timer.pause()
            }
            MediaSynthStatus.START -> {
                onStart(oldCode == MediaSynthStatus.CREATE)
            }
            MediaSynthStatus.CREATE -> {
                timer.stop()
                resetProgress()
            }
            else -> {
                timer.stop()
                onStop()
            }
        }
    }

    protected fun changeToStatus(newCode: Int): Boolean {
        val oldCode = status()
        if (enableChangeStatus(oldCode, newCode)) {
            statusCode.set(newCode)
            onStateChanged(oldCode, newCode)
            statusListenerSet.forEach {
                it.onStatusChanged(oldCode, newCode)
            }
            return true
        }
        return false
    }

    protected open fun enableChangeStatus(oldCode: Int, newCode: Int): Boolean {
        val enable = when (newCode) {
            MediaSynthStatus.CREATE -> {
                oldCode == MediaSynthStatus.STOP || oldCode == MediaSynthStatus.COMPLETE
            }
            MediaSynthStatus.START -> {
                oldCode == MediaSynthStatus.CREATE || oldCode == MediaSynthStatus.PAUSE
            }
            MediaSynthStatus.PAUSE -> {
                oldCode == MediaSynthStatus.START
            }
            MediaSynthStatus.STOP -> {
                oldCode == MediaSynthStatus.START || oldCode == MediaSynthStatus.PAUSE
            }
            MediaSynthStatus.COMPLETE -> {
                oldCode == MediaSynthStatus.START || oldCode == MediaSynthStatus.PAUSE
            }
            else -> {
                true
            }
        }
        return enable
    }

    protected fun onProgressUpdate() {
        val p1 = min(
            videoTrackRecorder.trackProgressTime.get(), audioTrackRecorder.trackProgressTime.get()
        )
        val p0 = lastProgressTime.get()
        if (p0 < p1) {
            lastProgressTime.set(p1)
            statusListenerSet.forEach {
                it.onProgress(p1, getDuration(), false)
            }
        }
    }

    protected fun dispatchDecoderOutput(
        trackId: Int,
        mediaInfo: MediaInfo,
        index: Int,
        decoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        encoder: MediaCodec?,
    ) {
        getTrackInputRecorder(trackId)?.outputListener?.onDecoderOutput(
            mediaInfo, index, decoder, bufferInfo, encoder
        )
    }

    protected fun dispatchEncoderOutput(
        trackId: Int,
        mediaInfo: MediaInfo,
        index: Int,
        encoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo
    ) {
        getTrackInputRecorder(trackId)?.outputListener?.onEncoderOutput(
            mediaInfo, index, encoder, bufferInfo
        )
    }

    protected fun getCurrentInputConfig(trackId: Int): IMediaSynthTrackInput? {
        return findInputConfig(trackId, 0)
    }

    protected fun getNextInputConfig(trackId: Int): IMediaSynthTrackInput? {
        return findInputConfig(trackId, 1)
    }

    protected fun findInputConfig(trackId: Int, offsetStartIndex: Int): IMediaSynthTrackInput? {
        val indexRecorder: AtomicInteger =
            getTrackInputRecorder(trackId)?.trackInputIndex ?: return null
        var inputIndex = indexRecorder.get()
        if (offsetStartIndex != 0) {
            inputIndex += offsetStartIndex
            indexRecorder.set(inputIndex)
        }
        while (inputIndex < inputs.size) {
            val inputConfig = inputs.getOrNull(inputIndex)
            val trackConfig = inputConfig?.getTrackInput(trackId)
            if (trackConfig?.hasInputConfig() == true) {
                return trackConfig
            }
            inputIndex++
            indexRecorder.set(inputIndex)
        }
        return null
    }

    protected fun getTrackInputRecorder(trackId: Int): MediaTrackRecorder? {
        return when (trackId) {
            MediaSynthTrack.VIDEO_TRACK -> {
                videoTrackRecorder
            }
            MediaSynthTrack.AUDIO_TRACK -> {
                audioTrackRecorder
            }
            else -> {
                null
            }
        }
    }

    @SuppressLint("WrongConstant")
    protected fun copyMedia(
        extractor: MediaExtractor,
        dataTrackId: Int,
        offsetTimeUs: Long = 0,
        progress: ((timeUs: Long) -> Unit)?
    ): Boolean {
        var byteBuffer = ByteBuffer.allocate(bufferSize)
        val outputInfo = MediaCodec.BufferInfo()
        var readSampleSize: Int
        var lastTimeUs=0L
        while (true) {
            if (requireInterruptedOrBlock()) {
                return false
            }
            readSampleSize = try {
                extractor.readSampleData(byteBuffer, 0)
            } catch (e: Exception) {
                e.printStackTrace()
                Int.MAX_VALUE
            }
            if (readSampleSize == Int.MAX_VALUE) {
                if (bufferSize == maxBufferSize) {
                    changeToStatus(MediaSynthStatus.BUFFER_SIZE_ERROR)
                    return false
                }
                bufferSize += minBufferSize
                byteBuffer = ByteBuffer.allocate(bufferSize)
                Thread.sleep(10L)
                continue
            }
            if (readSampleSize > 0) {
                outputInfo.presentationTimeUs = extractor.sampleTime + offsetTimeUs
                outputInfo.offset = 0
                outputInfo.size = readSampleSize
                outputInfo.flags = extractor.sampleFlags
                outputWriter.write(dataTrackId, byteBuffer, outputInfo)
                progress?.invoke(outputInfo.presentationTimeUs)
                lastTimeUs= outputInfo.presentationTimeUs
                extractor.advance()
                byteBuffer.clear()
            } else {
                Log.i(TAG,"finish copyMedia==>${lastTimeUs}")
                break
            }
        }
        return true
    }

    protected fun readSampleData(extractor: MediaExtractor, byteBuffer: ByteBuffer): Int {
        return try {
            extractor.readSampleData(byteBuffer, 0)
        } catch (e: Exception) {
            e.printStackTrace()
            Int.MAX_VALUE
        }
    }

    protected fun isFinished(): Boolean {
        return videoTrackRecorder.trackInputIndex.get() >= inputs.size && audioTrackRecorder.trackInputIndex.get() >= inputs.size
    }

    protected fun requireInterruptedOrBlock(): Boolean {
        var code = status()
        return when (code) {
            MediaSynthStatus.STOP -> {
                true
            }
            MediaSynthStatus.PAUSE -> {
                synchronized(workThreadLock) {
                    code = status()
                    return when (code) {
                        MediaSynthStatus.STOP -> {
                            true
                        }
                        MediaSynthStatus.PAUSE -> {
                            try {
                                workThreadLock.wait()
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            status() == MediaSynthStatus.STOP
                        }
                        else -> false
                    }
                }
            }
            else -> false
        }
    }

    protected open fun resetProgress() {
        lastProgressTime.set(0L)
        videoTrackRecorder.reset()
        audioTrackRecorder.reset()
    }

    fun getTakeTime(): Long {
        return timer.getTime()
    }

    protected abstract fun onStart(initConfig: Boolean)
    protected abstract fun onStop()
}