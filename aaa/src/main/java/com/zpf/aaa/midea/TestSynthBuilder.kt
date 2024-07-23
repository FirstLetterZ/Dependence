package com.zpf.aaa.midea

import android.content.Context
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import java.io.FileDescriptor

class TestSynthBuilder : AbsSynthBuilder {
    constructor(path: String, headers: Map<String, String>?) : super(path, headers)
    constructor(context: Context, uri: Uri, headers: Map<String, String>?) : super(
        context, uri, headers
    )

    constructor(fd: FileDescriptor) : super(fd)

    override fun createSynthesizer(
        videoUnit: MediaUnit?,
        audioUnit: MediaUnit?,
        mediaMuxer: MediaMuxer,
        retriever: MediaMetadataRetriever,
        mediaInfo: MediaInfo
    ): IMediaSynth {
        return TestSynth(videoUnit, audioUnit, mediaMuxer, retriever, mediaInfo)
//        return TestSynth(videoUnit, null, mediaMuxer, retriever, mediaInfo)
    }

    override fun buildDecoderMediaFormat(
        mediaInfo: MediaInfo, originalMediaFormat: MediaFormat
    ): MediaFormat {
        val mimeStr = originalMediaFormat.getString(MediaFormat.KEY_MIME) ?: ""
        if (mimeStr.startsWith("video/")) {
            originalMediaFormat.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
            )
        }
        return originalMediaFormat
    }

    override fun buildEncoderMediaFormat(
        mediaInfo: MediaInfo, originalMediaFormat: MediaFormat
    ): MediaFormat {
        val mimeStr = originalMediaFormat.getString(MediaFormat.KEY_MIME) ?: ""
        if (mimeStr.startsWith("video/")) {
            return buildVideoEncoderMediaFormat(
                mediaInfo, mimeStr, originalMediaFormat
            )
        }else  if (mimeStr.startsWith("audio/")) {
            return buildAudioEncoderMediaFormat(
                mediaInfo, mimeStr, originalMediaFormat
            )
        }
        return originalMediaFormat
    }

    protected fun buildVideoEncoderMediaFormat(
        mediaInfo: MediaInfo, mime: String, originalMediaFormat: MediaFormat
    ): MediaFormat {
        // https://developer.android.google.cn/reference/android/media/MediaCodec mediacodec官方介绍
        // 比方MediaCodec的几种状态
        // avc即h264编码
        val mediaFormat =
//            MediaFormat.createVideoFormat(mime, mediaInfo.getTrueWidth(), mediaInfo.getTrueHeight())
            MediaFormat.createVideoFormat(mime, mediaInfo.width, mediaInfo.height)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaFormat.setInteger(
                MediaFormat.KEY_ROTATION, originalMediaFormat.getInteger(MediaFormat.KEY_ROTATION)
            )
        }
        // 设置颜色格式
        // 本地原始视频格式（native raw video format）：这种格式通过COLOR_FormatSurface标记，并可以与输入或输出Surface一起使用
        mediaFormat.setInteger(
//            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        // 设置码率，通常码率越高，视频越清晰，但是对应的视频也越大
        mediaFormat.setInteger(
            MediaFormat.KEY_BIT_RATE, mediaInfo.width * mediaInfo.height * 4
        )
//        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 10000000)

        // 设置帧率 三星s21手机camera预览时，支持的帧率为10-30
        // 通常这个值越高，视频会显得越流畅，一般默认设置成30，你最低可以设置成24，不要低于这个值，低于24会明显卡顿，微信为28
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        // 设置 I 帧间隔的时间
        // 通常的方案是设置为 1s，对于图片电影等等特殊情况，这里可以设置为 0，表示希望每一帧都是 KeyFrame
        // IFRAME_INTERVAL是指的帧间隔，这是个很有意思的值，它指的是，关键帧的间隔时间。通常情况下，你设置成多少问题都不大。
        // 比如你设置成10，那就是10秒一个关键帧。但是，如果你有需求要做视频的预览，那你最好设置成1
        // 因为如果你设置成10，那你会发现，10秒内的预览都是一个截图
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

        // 创建编码器
        // https://www.codercto.com/a/41316.html MediaCodec 退坑指南
//            videoEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        return mediaFormat
    }

    private fun buildAudioEncoderMediaFormat(
        mediaInfo: MediaInfo, mime: String, originalMediaFormat: MediaFormat
    ): MediaFormat {
        val mediaFormat = MediaFormat.createAudioFormat(
            mime, 44100, 2
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000) //比特率
        mediaFormat.setInteger(
            MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC
        )
        return mediaFormat

    }
}