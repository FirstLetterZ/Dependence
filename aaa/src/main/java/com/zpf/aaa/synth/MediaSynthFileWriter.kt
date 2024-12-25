package  com.zpf.aaa.synth

import android.media.MediaCodec
import android.media.MediaFormat
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class MediaSynthFileWriter(
    outputFilePath: String
) : ISynthOutputWriter {
    private val fos = FileOutputStream(outputFilePath)
    private val videoTrackIndex = AtomicInteger(-1)
    private val audioTrackIndex = AtomicInteger(-1)
    private val isRunning = AtomicBoolean(false)

    override fun setFormat(trackId: Int, mediaFormat: MediaFormat?): Int {
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
            indexRecord.set(0)
        }
        start()
        return indexRecord.get()
    }

    override fun write(trackId: Int, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo) {
        if (isRunning.get()) {
            doWriteData(trackId, buffer, outputInfo)
        }
    }

    override fun isFormatted(trackId: Int): Boolean {
        return getWriteIndex(trackId) >= 0
    }

    override fun start() {
        isRunning.set(true)
    }

    override fun stop() {
        isRunning.set(false)
    }

    private fun getWriteIndex(trackId: Int): Int {
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

    private fun doWriteData(
        trackId: Int, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo
    ) {
        val writeIndex = getWriteIndex(trackId)
        if (writeIndex < 0 || writeIndex == Int.MAX_VALUE || !isRunning.get()) {
            return
        }
        val bytes = ByteArray(outputInfo.size)
        buffer.get(bytes)
        fos.write(bytes)
    }

}