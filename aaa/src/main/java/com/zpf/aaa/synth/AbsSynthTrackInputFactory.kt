package com.zpf.aaa.synth

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.FileDescriptor

abstract class AbsSynthTrackInputFactory {

    open fun create(fd: FileDescriptor): MediaSynthInput? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(fd)
        val mediaInfo = MediaInfo(retriever)
        if (mediaInfo.duration <= 0) {
            return null
        }
        val videoExtractor = MediaExtractor()
        val audioExtractor = MediaExtractor()
        videoExtractor.setDataSource(fd)
        audioExtractor.setDataSource(fd)
        return build(mediaInfo, videoExtractor, audioExtractor)

    }

    open fun create(path: String, headers: Map<String, String>?): MediaSynthInput? {
        val retriever = MediaMetadataRetriever()
        if (headers == null) {
            retriever.setDataSource(path)
        } else {
            retriever.setDataSource(path, headers)
        }
        val mediaInfo = MediaInfo(retriever)
        if (mediaInfo.duration <= 0) {
            return null
        }
        val videoExtractor = MediaExtractor()
        val audioExtractor = MediaExtractor()
        if (headers == null) {
            videoExtractor.setDataSource(path)
            audioExtractor.setDataSource(path)
        } else {
            videoExtractor.setDataSource(path, headers)
            audioExtractor.setDataSource(path, headers)
        }
        return build(mediaInfo, videoExtractor, audioExtractor)
    }

    open fun create(context: Context, uri: Uri, headers: Map<String, String>?): MediaSynthInput? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val mediaInfo = MediaInfo(retriever)
        if (mediaInfo.duration <= 0) {
            return null
        }
        val videoExtractor = MediaExtractor()
        val audioExtractor = MediaExtractor()
        videoExtractor.setDataSource(context, uri, headers)
        audioExtractor.setDataSource(context, uri, headers)
        return build(mediaInfo, videoExtractor, audioExtractor)
    }

    private fun build(
        mediaInfo: MediaInfo, videoExtractor: MediaExtractor, audioExtractor: MediaExtractor
    ): MediaSynthInput? {
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
        val videoTrackInput = if (videoTrackIndex >= 0 && videoDecoderFormat != null) {
            createVideoTrackInputConfig(
                mediaInfo, videoTrackIndex, videoDecoderFormat, videoExtractor
            )
        } else {
            null
        }
        val audioTrackInput = if (audioTrackIndex >= 0 && audioDecoderFormat != null) {
            createAudioTrackInputConfig(
                mediaInfo, audioTrackIndex, audioDecoderFormat, audioExtractor
            )
        } else {
            null
        }
        if (videoTrackInput == null && audioDecoderFormat == null) {
            return null
        }
        return MediaSynthInput(mediaInfo, videoTrackInput, audioTrackInput)
    }

    protected open fun createVideoTrackInputConfig(
        mediaInfo: MediaInfo, trackIndex: Int, format: MediaFormat, extractor: MediaExtractor
    ): IMediaSynthTrackInput? {
        val originalMimeStr = format.getString(MediaFormat.KEY_MIME) ?: ""
        val decoderMediaFormat = buildVideoDecoderMediaFormat(mediaInfo, originalMimeStr, format)
        val encoderMediaFormat = buildVideoEncoderMediaFormat(mediaInfo, originalMimeStr, format)
        val decoderMine = decoderMediaFormat?.getString(MediaFormat.KEY_MIME) ?: ""
        val encoderMine = encoderMediaFormat?.getString(MediaFormat.KEY_MIME) ?: ""
        var decoder: MediaCodec? = null
        var encoder: MediaCodec? = null
        if (decoderMediaFormat != null && decoderMine.isNotEmpty()) {
            decoder = MediaCodec.createDecoderByType(decoderMine)
        }
        if (encoderMediaFormat != null && encoderMine.isNotEmpty()) {
            encoder = MediaCodec.createEncoderByType(encoderMine)
        }
        if (decoder == null && encoder == null) {
            return MediaExtractorInput(trackIndex, extractor)
        }
        return MediaCodecInput(
            trackIndex, extractor, decoder, decoderMediaFormat, encoder, encoderMediaFormat
        )
    }

    protected open fun createAudioTrackInputConfig(
        mediaInfo: MediaInfo, trackIndex: Int, format: MediaFormat, extractor: MediaExtractor
    ): IMediaSynthTrackInput? {
        val originalMimeStr = format.getString(MediaFormat.KEY_MIME) ?: ""
        val decoderMediaFormat = buildAudioDecoderMediaFormat(mediaInfo, originalMimeStr, format)
        val encoderMediaFormat = buildAudioEncoderMediaFormat(mediaInfo, originalMimeStr, format)
        val decoderMine = decoderMediaFormat?.getString(MediaFormat.KEY_MIME) ?: ""
        val encoderMine = encoderMediaFormat?.getString(MediaFormat.KEY_MIME) ?: ""
        var decoder: MediaCodec? = null
        var encoder: MediaCodec? = null
        if (decoderMediaFormat != null && decoderMine.isNotEmpty()) {
            decoder = MediaCodec.createDecoderByType(decoderMine)
        }
        if (encoderMediaFormat != null && encoderMine.isNotEmpty()) {
            encoder = MediaCodec.createEncoderByType(encoderMine)
        }
        if (decoder == null && encoder == null) {
            return MediaExtractorInput(trackIndex, extractor)
        }
        return MediaCodecInput(
            trackIndex, extractor, decoder, decoderMediaFormat, encoder, encoderMediaFormat
        )
    }

    protected abstract fun buildVideoDecoderMediaFormat(
        mediaInfo: MediaInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat?

    protected abstract fun buildVideoEncoderMediaFormat(
        mediaInfo: MediaInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat?

    protected abstract fun buildAudioDecoderMediaFormat(
        mediaInfo: MediaInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat?

    protected abstract fun buildAudioEncoderMediaFormat(
        mediaInfo: MediaInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat?
}