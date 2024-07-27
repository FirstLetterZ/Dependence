package com.zpf.aaa.midea

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import java.io.FileDescriptor

class TestSynthBuilder : AbsSynthBuilder {
    constructor(path: String, headers: Map<String, String>?) : super(path, headers)
    constructor(context: Context, uri: Uri, headers: Map<String, String>?) : super(
        context, uri, headers
    )

    constructor(fd: FileDescriptor) : super(fd)

    override fun createSynthesizer(
        videoTrack: MediaTrackInfo?,
        audioTrack: MediaTrackInfo?,
        mediaMuxer: MediaMuxer,
        retriever: MediaMetadataRetriever,
        mediaInfo: MediaInfo
    ): IMediaSynth {
        return SyncMediaSynth(
            videoTrack as MediaCodecTrackInfo, audioTrack, mediaMuxer, retriever, mediaInfo
        )
    }

    override fun createVideoTrackInfo(
        trackIndex: Int, originalMediaFormat: MediaFormat, extractor: MediaExtractor
    ): MediaCodecTrackInfo {
        val decoderMediaFormat = buildDecoderMediaFormat(mediaInfo, originalMediaFormat)
        val encoderMediaFormat = buildEncoderMediaFormat(mediaInfo, originalMediaFormat)
        val decoderMine = decoderMediaFormat.getString(MediaFormat.KEY_MIME) ?: ""
        val encoderMine = encoderMediaFormat.getString(MediaFormat.KEY_MIME) ?: ""
        return MediaCodecTrackInfo(
            trackIndex,
            extractor,
            decoderMediaFormat,
            encoderMediaFormat,
            MediaCodec.createDecoderByType(decoderMine),
            MediaCodec.createEncoderByType(encoderMine)
        )
    }

    override fun buildDecoderMediaFormat(
        mediaInfo: MediaInfo, originalMediaFormat: MediaFormat
    ): MediaFormat {
        val mimeStr = originalMediaFormat.getString(MediaFormat.KEY_MIME) ?: ""
        if (mimeStr.startsWith("video/")) {
            originalMediaFormat.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
//                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV444Flexible
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
        } else if (mimeStr.startsWith("audio/")) {
            return buildAudioEncoderMediaFormat(
                mediaInfo, mimeStr, originalMediaFormat
            )
        }
        return originalMediaFormat
    }

    protected fun buildVideoEncoderMediaFormat(
        mediaInfo: MediaInfo, mime: String, originalMediaFormat: MediaFormat
    ): MediaFormat {
        val mediaFormat =
//            MediaFormat.createVideoFormat(mime, mediaInfo.getTrueWidth(), mediaInfo.getTrueHeight())
            MediaFormat.createVideoFormat(mime, mediaInfo.getTrueWidth(), mediaInfo.getTrueHeight())
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mediaFormat.setInteger(
//                MediaFormat.KEY_ROTATION, originalMediaFormat.getInteger(MediaFormat.KEY_ROTATION)
//            )
//        }
        mediaFormat.setInteger(
//            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(
//            MediaFormat.KEY_BIT_RATE, 3000000
            MediaFormat.KEY_BIT_RATE, mediaInfo.width * mediaInfo.height * 4
        )
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
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