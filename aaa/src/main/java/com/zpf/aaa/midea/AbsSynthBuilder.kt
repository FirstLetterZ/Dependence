package com.zpf.aaa.midea

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.util.Log
import java.io.FileDescriptor

abstract class AbsSynthBuilder {
    private val videoExtractor: MediaExtractor = MediaExtractor()
    private val audioExtractor: MediaExtractor = MediaExtractor()
    val retriever = MediaMetadataRetriever()
    val mediaInfo: MediaInfo

    constructor(fd: FileDescriptor) {
        retriever.setDataSource(fd)
        mediaInfo = initMediaInfo(retriever)
        if (mediaInfo.duration > 0) {
            videoExtractor.setDataSource(fd)
            audioExtractor.setDataSource(fd)
        }
    }

    constructor(path: String, headers: Map<String, String>?) {
        if (headers == null) {
            retriever.setDataSource(path)
        } else {
            retriever.setDataSource(path, headers)
        }
        mediaInfo = initMediaInfo(retriever)
        if (mediaInfo.duration > 0) {
            if (headers == null) {
                videoExtractor.setDataSource(path)
                audioExtractor.setDataSource(path)
            } else {
                videoExtractor.setDataSource(path, headers)
                audioExtractor.setDataSource(path, headers)
            }
        }
    }

    constructor(context: Context, uri: Uri, headers: Map<String, String>?) {
        retriever.setDataSource(context, uri)
        mediaInfo = initMediaInfo(retriever)
        if (mediaInfo.duration > 0) {
            videoExtractor.setDataSource(context, uri, headers)
            audioExtractor.setDataSource(context, uri, headers)
        }
    }

    fun build(
        outputFilePath: String, format: Int = MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
    ): IMediaSynth? {
        if (mediaInfo.duration <= 0) {
            return null
        }
        var videoTrackIndex = -1
        var videoDecoderFormat: MediaFormat? = null
        var audioTrackIndex = -1
        var audioDecoderFormat: MediaFormat? = null
        val extractor = videoExtractor
        for (i in 0 until extractor.trackCount) {
            val trackFormat = extractor.getTrackFormat(i)
            val mime = trackFormat.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                videoTrackIndex = i
                videoDecoderFormat = trackFormat
            } else if (mime?.startsWith("audio/") == true) {
                audioTrackIndex = i
                audioDecoderFormat = trackFormat
            }
        }
        if (videoDecoderFormat == null && audioDecoderFormat == null) {
            return null
        }
        val mediaMuxer = MediaMuxer(outputFilePath, format)
        var videoTrack: MediaTrackInfo? = null
        var audioTrack: MediaTrackInfo? = null
        if (videoDecoderFormat != null) {
            videoTrack = createVideoTrackInfo(videoTrackIndex, videoDecoderFormat, videoExtractor)
        }
        if (audioDecoderFormat != null) {
            audioTrack = createAudioTrackInfo(audioTrackIndex, audioDecoderFormat, audioExtractor)
        }
        return createSynthesizer(videoTrack, audioTrack, mediaMuxer, retriever, mediaInfo)
    }

    protected abstract fun createSynthesizer(
        videoTrack: MediaTrackInfo?,
        audioTrack: MediaTrackInfo?,
        mediaMuxer: MediaMuxer,
        retriever: MediaMetadataRetriever,
        mediaInfo: MediaInfo
    ): IMediaSynth

    protected abstract fun buildDecoderMediaFormat(
        mediaInfo: MediaInfo, originalMediaFormat: MediaFormat
    ): MediaFormat

    protected abstract fun buildEncoderMediaFormat(
        mediaInfo: MediaInfo, originalMediaFormat: MediaFormat
    ): MediaFormat

    protected open fun createVideoTrackInfo(
        trackIndex: Int, originalMediaFormat: MediaFormat, extractor: MediaExtractor
    ): MediaTrackInfo {
        return MediaTrackInfo(trackIndex, extractor)
    }

    protected open fun createAudioTrackInfo(
        trackIndex: Int, originalMediaFormat: MediaFormat, extractor: MediaExtractor
    ): MediaTrackInfo {
        return MediaTrackInfo(trackIndex, extractor)
    }

    private fun initMediaInfo(mediaMetadataRetriever: MediaMetadataRetriever): MediaInfo {
        val mime =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                ?: ""
        val width =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toInt() ?: 0
        val height =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toInt() ?: 0
        val rotation =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                ?.toInt() ?: 0
        val duration =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong() ?: 0L
        return MediaInfo(mime, width, height, rotation, duration)
    }

}