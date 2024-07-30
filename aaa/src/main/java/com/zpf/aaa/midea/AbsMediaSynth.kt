package com.zpf.aaa.midea

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.view.Surface
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

abstract class AbsMediaSynth(
    protected val videoTrack: MediaTrackInfo?,
    protected val audioTrack: MediaTrackInfo?,
    protected val mediaMuxer: MediaMuxer,
    val retriever: MediaMetadataRetriever,
    val mediaInfo: MediaInfo
) : IMediaSynth {

    protected val statusCode = AtomicInteger(0)
    protected val threadLock = Object()
    protected val muxerLock = Object()
    protected val muxerStarted = AtomicBoolean(false)
    protected var progressListener: ISynthProgressListener? = null
    protected var audioCodecListener: ISynthCodecListener? = null
    protected var videoCodecListener: ISynthCodecListener? = null
    protected val totalDurationUs = mediaInfo.duration * 1000L
    protected var videoProgress: Long = Long.MAX_VALUE
    protected var audioProgress: Long = Long.MAX_VALUE
    protected var videoThread: Thread? = null
    protected var audioThread: Thread? = null
    private var encoderInputSurface: Surface? = null
    private var startTime = System.currentTimeMillis()

    override fun setTimeListener(listener: ISynthProgressListener?) {
        progressListener = listener
    }

    override fun setVideoListener(listener: ISynthCodecListener?) {
        videoCodecListener = listener
    }

    override fun setAudioListener(listener: ISynthCodecListener?) {
        audioCodecListener = listener
    }

    override fun getInputInfo(i: Int): MediaSynthInput? {
        return null
    }

    override fun getDecodeSurface(): Surface? {
        return null
    }

    override fun getEncodeSurface(): Surface? {
        return encoderInputSurface
    }

    final override fun status(): Int = statusCode.get()

    override fun start() {
        val code = status()
        if (code == MediaSynthStatus.CREATE) {
            startTime = System.currentTimeMillis()
            statusCode.set(MediaSynthStatus.START)
            onStateChanged(code, MediaSynthStatus.START)
        } else if (code == MediaSynthStatus.PAUSE) {
            statusCode.set(MediaSynthStatus.START)
            onStateChanged(code, MediaSynthStatus.START)
        }
    }

    override fun pause() {
        val code = status()
        if (code == MediaSynthStatus.START) {
            statusCode.set(MediaSynthStatus.PAUSE)
            onStateChanged(code, MediaSynthStatus.PAUSE)
        }
    }

    override fun stop() {
        val code = status()
        if (code == MediaSynthStatus.START || code == MediaSynthStatus.PAUSE) {
            statusCode.set(MediaSynthStatus.STOP)
            onStateChanged(code, MediaSynthStatus.STOP)
        }
    }

    override fun reset() {
        val code = status()
        if (code == MediaSynthStatus.STOP) {
            statusCode.set(MediaSynthStatus.CREATE)
        }
    }

    protected open fun onReleased() {
        if (muxerStarted.get()) {
            muxerStarted.set(false)
            mediaMuxer.stop()
            mediaMuxer.release()
        }
        videoTrack?.let {
            it.extractor.release()
            if (it is MediaCodecTrackInfo) {
                if (it.coderStarted.get()) {
                    it.coderStarted.set(false)
                    it.decoder.stop()
                    it.encoder.stop()
                }
                it.decoder.release()
                it.encoder.release()
            }
        }
        audioTrack?.let {
            it.extractor.release()
            if (it is MediaCodecTrackInfo) {
                if (it.coderStarted.get()) {
                    it.coderStarted.set(false)
                    it.decoder.stop()
                    it.encoder.stop()
                }
                it.decoder.release()
                it.encoder.release()
            }
        }
    }

    protected open fun onStateChanged(oldCode: Int, newCode: Int) {
        when (newCode) {
            MediaSynthStatus.STOP -> {
                videoTrack?.completed?.set(0)
                audioTrack?.completed?.set(0)
                if (videoThread?.isAlive != true && audioThread?.isAlive != true) {
                    onReleased()
                } else {
                    synchronized(threadLock) {
                        if (videoThread?.isAlive != true && audioThread?.isAlive != true) {
                            onReleased()
                        } else {
                            threadLock.notifyAll()
                        }
                    }
                }
            }
            MediaSynthStatus.PAUSE -> {

            }
            MediaSynthStatus.START -> {
                if (oldCode == MediaSynthStatus.CREATE) {
                    videoTrack?.let {
                        configureVideoTrack(it)
                    }
                    audioTrack?.let {
                        configureAudioTrack(it)
                    }
                }
                videoTrack?.let {
                    startVideoTrack(it)
                }
                audioTrack?.let {
                    startAudioTrack(it)
                }
            }
        }
    }

    protected open fun configureVideoTrack(trackInfo: MediaTrackInfo) {
        videoProgress = 0L
        trackInfo.extractor.selectTrack(trackInfo.sourceTrackIndex)
        trackInfo.completed.set(-1)
        if (trackInfo is MediaCodecTrackInfo) {
            trackInfo.decoder.configure(trackInfo.decodeFormat, null, null, 0)
            trackInfo.encoder.configure(
                trackInfo.encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE
            )
            if (trackInfo.encodeFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT) == MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface) {
                if (encoderInputSurface == null) {
                    encoderInputSurface = trackInfo.encoder.createInputSurface()
                }
            }
        }
    }

    protected open fun configureAudioTrack(trackInfo: MediaTrackInfo) {
        audioProgress = 0L
        trackInfo.extractor.selectTrack(trackInfo.sourceTrackIndex)
        trackInfo.completed.set(-1)
        if (trackInfo is MediaCodecTrackInfo) {
            trackInfo.decoder.configure(trackInfo.decodeFormat, null, null, 0)
            trackInfo.encoder.configure(
                trackInfo.encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE
            )
        }
    }

    protected abstract fun startAudioTrack(trackInfo: MediaTrackInfo)
    protected abstract fun startVideoTrack(trackInfo: MediaTrackInfo)

    protected open fun enableCodec(): Boolean {
        return status() == MediaSynthStatus.START
    }

    protected open fun onVideoEncodeFormatChanged(mediaFormat: MediaFormat) {
        val index = mediaMuxer.addTrack(mediaFormat)
        videoTrack?.muxerTrackIndex?.set(index)
        checkStartMuxer()
    }

    protected open fun onAudioEncodeFormatChanged(mediaFormat: MediaFormat) {
        val index = mediaMuxer.addTrack(mediaFormat)
        audioTrack?.muxerTrackIndex?.set(index)
        checkStartMuxer()
    }

    protected fun checkStartMuxer() {
        synchronized(muxerLock) {
            if (!muxerStarted.get() && (videoTrack?.muxerTrackIndex?.get()
                    ?: 1) >= 0 && (audioTrack?.muxerTrackIndex?.get() ?: 1) >= 0
            ) {
                mediaMuxer.start()
                muxerStarted.set(true)
                muxerLock.notify()
            }
        }
    }

    @SuppressLint("WrongConstant")
    protected fun copyMedia(
        trackInfo: MediaTrackInfo, progress: ((timeUs: Long) -> Unit)?
    ): Boolean {
        val byteBuffer = ByteBuffer.allocate(500 * 1024)
        val outputInfo = MediaCodec.BufferInfo()
        while (true) {
            if (checkInterruptedThread()) {
                return false
            }
            val readSampleData = trackInfo.extractor.readSampleData(byteBuffer, 0)
            if (readSampleData > 0) {
                outputInfo.presentationTimeUs = trackInfo.extractor.sampleTime
                outputInfo.offset = 0
                outputInfo.size = readSampleData
                outputInfo.flags = trackInfo.extractor.sampleFlags
                writeMuxerData(trackInfo.muxerTrackIndex.get(), byteBuffer, outputInfo)
                progress?.invoke(outputInfo.presentationTimeUs)
                trackInfo.extractor.advance()
                byteBuffer.clear()
            } else {
                break
            }
        }
        return true
    }

    protected fun writeMuxerData(
        index: Int, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo
    ) {
        if (!muxerStarted.get()) {
            synchronized(muxerLock) {
                if (!muxerStarted.get()) {
                    try {
                        muxerLock.wait()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    if (status() != MediaSynthStatus.STOP) {
                        mediaMuxer.writeSampleData(index, buffer, outputInfo)
                    }
                }
            }
        } else {
            mediaMuxer.writeSampleData(index, buffer, outputInfo)
        }
    }

    protected fun onVideoProgressUpdate(timeUs: Long) {
        videoProgress = timeUs
        progressListener?.let {
            val p = min(videoProgress, audioProgress)
            it.onProgress(p, totalDurationUs, false)
        }
    }

    protected fun onAudioProgressUpdate(timeUs: Long) {
        audioProgress = timeUs
        progressListener?.let {
            val p = min(videoProgress, audioProgress)
            it.onProgress(p, totalDurationUs, false)
        }
    }

    protected fun checkFinish() {
        if ((videoTrack?.completed?.get() ?: 1) >= 0 && (audioTrack?.completed?.get() ?: 1) >= 0) {
            if (statusCode.get() == MediaSynthStatus.STOP) {
                onReleased()
            } else {
                progressListener?.onProgress(totalDurationUs, totalDurationUs, true)
                videoThread = null
                audioThread = null
                stop()
            }
        }
    }

    protected fun checkInterruptedThread(): Boolean {
        var code = statusCode.get()
        return when (code) {
            MediaSynthStatus.STOP -> {
                true
            }
            MediaSynthStatus.PAUSE -> {
                synchronized(threadLock) {
                    code = statusCode.get()
                    return when (code) {
                        MediaSynthStatus.STOP -> {
                            return true
                        }
                        MediaSynthStatus.PAUSE -> {
                            try {
                                threadLock.wait()
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            statusCode.get() == MediaSynthStatus.STOP
                        }
                        else -> false
                    }
                }
            }
            else -> false
        }
    }

}