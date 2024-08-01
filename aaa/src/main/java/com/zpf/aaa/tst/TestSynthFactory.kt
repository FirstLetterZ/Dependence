package com.zpf.aaa.tst

import android.media.MediaCodecInfo
import android.media.MediaFormat
import com.zpf.aaa.synth.AbsSynthTrackInputFactory
import com.zpf.aaa.synth.MediaInfo

class TestSynthFactory : AbsSynthTrackInputFactory() {

    override fun buildVideoDecoderMediaFormat(
        mediaInfo: MediaInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        originalMediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
        )
        return originalMediaFormat
    }

    override fun buildVideoEncoderMediaFormat(
        mediaInfo: MediaInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        val mediaFormat = MediaFormat.createVideoFormat(
            mimeStr, mediaInfo.getTrueWidth(), mediaInfo.getTrueHeight()
        )
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        //设置视频的编码参数
        val bitRate = if (mediaInfo.height > 3500 || mediaInfo.width > 3500) {
            10000000
        } else {
            3000000
        }
        mediaFormat.setInteger(
            MediaFormat.KEY_BIT_RATE, bitRate
        )
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        return mediaFormat
    }

    override fun buildAudioDecoderMediaFormat(
        mediaInfo: MediaInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        return null
    }

    override fun buildAudioEncoderMediaFormat(
        mediaInfo: MediaInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        return null
    }
}