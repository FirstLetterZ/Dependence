package com.zpf.aaa.tst

import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Size
import com.zpf.aaa.synth.AbsSynthBuilder
import com.zpf.aaa.synth.ISynthOutputWriter
import com.zpf.aaa.synth.MediaInputBasicInfo
import com.zpf.aaa.synth.MediaOutputBasicInfo
import com.zpf.aaa.synth.MediaSynthOutput
import com.zpf.aaa.synth.SyncMediaSynth

class TestSyncSynthBuilder(outputFilePath: String) : AbsSynthBuilder<SyncMediaSynth>() {
    init {
        outputWriter = MediaSynthOutput(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    private val frameRate = 30

    override fun createSynth(
        basicInfo: MediaOutputBasicInfo, writer: ISynthOutputWriter
    ): SyncMediaSynth {
        return SyncMediaSynth(inputList, writer, basicInfo)
    }

    override fun createVideoDecoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat {
        return originalMediaFormat
    }

    override fun createVideoEncoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat {
        val cacheFormat = getOrCreateOutputBasicInfo(basicInfo)
        val mediaFormat =
            MediaFormat.createVideoFormat(mimeStr, cacheFormat.width, cacheFormat.height)
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 3000000)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, cacheFormat.frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
        return mediaFormat
    }

    private fun getOrCreateOutputBasicInfo(basicInfo: MediaInputBasicInfo): MediaOutputBasicInfo {
        val cacheInfo = outputBasicInfo
        if (cacheInfo != null) {
            return cacheInfo
        }
        val width = basicInfo.getTrueWidth()
        val height = basicInfo.getTrueHeight()
        val size = if (width > height) {
            val s = 1920.0f / width
            Size(1920, (height * s).toInt())
        } else {
            val s = 1920.0f / height
            Size((width * s).toInt(), 1920)
        }
        val info = MediaOutputBasicInfo(
            basicInfo.fileMime, width, height, basicInfo.duration, frameRate
        )
        outputBasicInfo = info
        return info
    }


}