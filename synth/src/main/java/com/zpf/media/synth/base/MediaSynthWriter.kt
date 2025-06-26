package com.zpf.media.synth.base

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import com.zpf.media.synth.i.ISynthInputPart
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaOutputBasicInfo
import com.zpf.media.synth.model.MediaSynthStatus
import com.zpf.media.synth.model.MediaSynthTrackId
import java.nio.ByteBuffer
import java.util.Arrays
import kotlin.math.max

abstract class MediaSynthWriter(
    outputInfo: MediaOutputBasicInfo,
    inputs: List<ISynthInputPart>,
    protected val writer: ISynthTrackWriter,
) : MediaSynthTaskManager(outputInfo, inputs) {
    protected var maxBufferSize = 16 * 1024 * 1024
    private val minBufferSize = 512 * 1024
    private var bufferSize = minBufferSize

    override fun onClear() {
        super.onClear()
        synchronized(writer) {
            writer.stop()
        }
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

    protected fun writeEmptyVoice(format: MediaFormat, starTimeUs: Long, endTimeUs: Long) {
        val frameRate = getOutputBasicInfo().frameRate
        if (frameRate < 1) {
            return
        }
        val frameUs = 1000_000L / frameRate
        val durationUs = endTimeUs - starTimeUs
        if (durationUs < frameUs) {
            return
        }
        // 采样率
        val sampleRateInHz = try {
            max(format.getInteger(MediaFormat.KEY_SAMPLE_RATE), 44100)
        } catch (e: Exception) {
            44100
        }
        val bitDepth = 16 // 位深（16 位）
        // 声道数
        val channels = try {
            max(format.getInteger(MediaFormat.KEY_CHANNEL_COUNT), 1)
        } catch (e: Exception) {
            1
        }
        val bufferSize = (frameUs * sampleRateInHz * channels * bitDepth / 2).toInt()
        val buffer = ByteBuffer.allocate(bufferSize)
        Arrays.fill(buffer.array(), 0)
        val bufferInfo = MediaCodec.BufferInfo()
        bufferInfo.offset = 0
        bufferInfo.size = bufferSize
        bufferInfo.presentationTimeUs = starTimeUs
        val trackId = MediaSynthTrackId.AUDIO
        writer.setFormat(trackId, format)
        while (bufferInfo.presentationTimeUs < endTimeUs) {
            writer.write(trackId, buffer, bufferInfo)
            bufferInfo.presentationTimeUs += frameUs
        }
    }

}