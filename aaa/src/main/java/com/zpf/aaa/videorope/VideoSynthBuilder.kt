package com.zpf.aaa.videorope

import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Size
import com.zpf.media.synth.AbsSynthBuilder
import com.zpf.media.synth.MediaSynthMuxerWriter
import com.zpf.media.synth.SyncMediaSynth
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaInputBasicInfo
import com.zpf.media.synth.model.MediaOutputBasicInfo
import kotlin.math.min

open class VideoSynthBuilder(outputFilePath: String) : AbsSynthBuilder<SyncMediaSynth>() {
    init {
        outputWriter =
            MediaSynthMuxerWriter(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    private val maxFrameRate = 30
//    private var bitRate = 3000000

    override fun createSynth(
        basicInfo: MediaOutputBasicInfo, writer: ISynthTrackWriter
    ): SyncMediaSynth {
        return SyncMediaSynth(basicInfo, inputList, writer)
    }

    override fun createVideoDecoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat {
        return originalMediaFormat
    }

    override fun createVideoEncoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        val frameRate =
            min(maxFrameRate, originalMediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE))
        val cacheFormat = getOrCreateOutputBasicInfo(basicInfo, frameRate)
        val mediaFormat =
            MediaFormat.createVideoFormat(mimeStr, cacheFormat.width, cacheFormat.height)
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(
            MediaFormat.KEY_BIT_RATE, cacheFormat.width * cacheFormat.height * 4
        )
//        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 3000000)

        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, cacheFormat.frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        return mediaFormat
    }

    private fun getOrCreateOutputBasicInfo(
        basicInfo: MediaInputBasicInfo, frameRate: Int
    ): MediaOutputBasicInfo {
        val cacheInfo = outputBasicInfo
        if (cacheInfo != null) {
            return cacheInfo
        }
        val width = basicInfo.getTrueWidth()
        val height = basicInfo.getTrueHeight()
        val targetMaxSize = 1280
//        val targetMaxSize = 1600
//        val targetMaxSize = 1920
        val size = if (width > height) {
            val s = targetMaxSize.toFloat() / width
            Size(targetMaxSize, (height * s).toInt())
        } else if (height > targetMaxSize) {
            val s = targetMaxSize.toFloat() / height
            Size((width * s).toInt(), targetMaxSize)
        } else {
            Size(width, height)
        }
//        val size = if (width > height) {
//            val s = targetMaxSize.toFloat() / width
//            Size(targetMaxSize, (height * s).toInt())
//        } else {
//            val s = targetMaxSize.toFloat() / height
//            Size((width * s).toInt(), targetMaxSize)
//        }

        val info = MediaOutputBasicInfo(
            basicInfo.fileMime, size.width, size.height, basicInfo.duration, frameRate
        )
        outputBasicInfo = info
        return info
    }
}