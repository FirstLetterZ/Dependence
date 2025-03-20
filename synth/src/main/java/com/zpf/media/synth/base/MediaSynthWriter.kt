package com.zpf.media.synth.base

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaExtractor
import com.zpf.media.synth.i.ISynthInputPart
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaOutputBasicInfo
import com.zpf.media.synth.model.MediaSynthStatus
import java.nio.ByteBuffer

abstract class MediaSynthWriter(
    outputInfo: MediaOutputBasicInfo,
    inputs: List<ISynthInputPart>,
    protected val writer: ISynthTrackWriter,
) : MediaSynthTaskManager(outputInfo, inputs) {
    protected var maxBufferSize = 4 * 1024 * 1024
    private val minBufferSize = 512 * 1024
    private var bufferSize = minBufferSize

    override fun onClear() {
        synchronized(writer) {
            writer.stop()
        }
        super.onClear()
    }

    @SuppressLint("WrongConstant")
    protected fun copyMedia(
        extractor: MediaExtractor,
        dataTrackId: String,
        offsetTimeUs: Long = 0,
        progress: ((timeUs: Long) -> Unit)?
    ): Boolean {
        var byteBuffer = ByteBuffer.allocate(bufferSize)
        val outputInfo = MediaCodec.BufferInfo()
        var readSampleSize: Int
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
                writer.write(dataTrackId, byteBuffer, outputInfo)
                progress?.invoke(outputInfo.presentationTimeUs)
                extractor.advance()
                byteBuffer.clear()
            } else {
                break
            }
        }
        return true
    }

}