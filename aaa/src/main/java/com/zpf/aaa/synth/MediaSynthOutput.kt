package com.zpf.aaa.synth

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class MediaSynthOutput(val outputFilePath: String, val format: Int) {
    private val videoTrackIndex = AtomicInteger(-1)
    private val audioTrackIndex = AtomicInteger(-1)

    @Volatile
    private var mediaMuxer: MediaMuxer? = null
    private val isRunning = AtomicBoolean(false)
    private val muxerLock = Object()


    fun setFormat(trackId: Int, mediaFormat: MediaFormat?): Int {
        val indexRecord = when (trackId) {
            MediaSynthTrack.VIDEO_TRACK -> {
                videoTrackIndex
            }
            MediaSynthTrack.AUDIO_TRACK -> {
                audioTrackIndex
            }
            else -> null
        } ?: return -1
        if (mediaFormat == null) {
            indexRecord.set(Int.MAX_VALUE)
        } else {
            val index = requireMuxer().addTrack(mediaFormat)
            indexRecord.set(index)
        }
        startMuxer()
        return indexRecord.get()
    }


    fun write(trackId: Int, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo) {
        if (!isRunning.get()) {
            synchronized(muxerLock) {
                if (!isRunning.get()) {
                    try {
                        muxerLock.wait()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                doWriteData(trackId, buffer, outputInfo)
            }
        } else {
            doWriteData(trackId, buffer, outputInfo)
        }
    }

    fun release() {
        if (isRunning.getAndSet(false)) {
            mediaMuxer?.run {
                mediaMuxer = null
                stop()
                release()
            }
            videoTrackIndex.set(-1)
            audioTrackIndex.set(-1)
        } else {
            synchronized(muxerLock) {
                muxerLock.notifyAll()
            }
        }
    }

    fun getWriteIndex(trackId: Int): Int {
        return when (trackId) {
            MediaSynthTrack.VIDEO_TRACK -> {
                videoTrackIndex.get()
            }
            MediaSynthTrack.AUDIO_TRACK -> {
                audioTrackIndex.get()
            }
            else -> -1
        }
    }

    fun isPrepared(): Boolean {
        return videoTrackIndex.get() >= 0 && audioTrackIndex.get() >= 0
    }

    private fun doWriteData(
        trackId: Int, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo
    ) {
        val writeIndex = getWriteIndex(trackId)
        if (writeIndex < 0 || writeIndex == Int.MAX_VALUE || !isRunning.get()) {
            return
        }
        mediaMuxer?.writeSampleData(writeIndex, buffer, outputInfo)
    }

    private fun requireMuxer(): MediaMuxer {
        val cacheMuxer = mediaMuxer
        if (cacheMuxer != null) {
            return cacheMuxer
        }
        return synchronized(this::class.java) {
            var cacheMuxer2 = mediaMuxer
            if (cacheMuxer2 == null) {
                cacheMuxer2 = MediaMuxer(outputFilePath, format)
                mediaMuxer = cacheMuxer2
            }
            cacheMuxer2
        }
    }

    private fun startMuxer() {
        if (isRunning.get()) {
            return
        }
        synchronized(muxerLock) {
            if (!isRunning.get() && videoTrackIndex.get() >= 0 && audioTrackIndex.get() >= 0) {
                requireMuxer().start()
                isRunning.set(true)
                muxerLock.notify()
            }
        }
    }

}