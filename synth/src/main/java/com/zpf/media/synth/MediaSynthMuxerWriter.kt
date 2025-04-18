package  com.zpf.media.synth

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaSynthTrackId
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class MediaSynthMuxerWriter(
    private val outputFilePath: String, private val formatCode: Int
) : ISynthTrackWriter {
    private val videoTrackIndex = AtomicInteger(-1)
    private val audioTrackIndex = AtomicInteger(-1)

    @Volatile
    private var mediaMuxer: MediaMuxer? = null
    private val isRunning = AtomicBoolean(false)
    private val muxerLock = Object()

    override fun setFormat(trackId: String, mediaFormat: MediaFormat?): Int {
        val indexRecord = when (trackId) {
            MediaSynthTrackId.VIDEO -> {
                videoTrackIndex
            }
            MediaSynthTrackId.AUDIO -> {
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
        start()
        return indexRecord.get()
    }

    override fun write(trackId: String, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo) {
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

    override fun isFormatted(trackId: String): Boolean {
        return getWriteIndex(trackId) >= 0
    }

    override fun start() {
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

    override fun stop() {
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

    private fun getWriteIndex(trackId: String): Int {
        return when (trackId) {
            MediaSynthTrackId.VIDEO -> {
                videoTrackIndex.get()
            }
            MediaSynthTrackId.AUDIO -> {
                audioTrackIndex.get()
            }
            else -> -1
        }
    }

    private fun doWriteData(
        trackId: String, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo
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
                cacheMuxer2 = MediaMuxer(outputFilePath, formatCode)
                mediaMuxer = cacheMuxer2
            }
            cacheMuxer2
        }
    }
}