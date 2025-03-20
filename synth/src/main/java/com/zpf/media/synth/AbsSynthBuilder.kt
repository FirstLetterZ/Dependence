package com.zpf.media.synth

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.zpf.media.synth.i.IMediaSynth
import com.zpf.media.synth.i.ISynthInputPart
import com.zpf.media.synth.i.ISynthTrackEditor
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaInputBasicInfo
import com.zpf.media.synth.model.MediaOutputBasicInfo
import com.zpf.media.synth.model.MediaSynthPartInfo
import com.zpf.media.synth.model.MediaSynthTrackId
import java.io.FileDescriptor

abstract class AbsSynthBuilder<T : IMediaSynth> {
    protected val inputList = ArrayList<ISynthInputPart>()
    protected var outputBasicInfo: MediaOutputBasicInfo? = null
    protected var outputWriter: ISynthTrackWriter? = null

    fun addInput(fd: FileDescriptor): ISynthInputPart? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(fd)
        val basicInfo = MediaInputBasicInfo(retriever)
        if (basicInfo.duration <= 0) {
            return null
        }
        val videoExtractor = MediaExtractor()
        val audioExtractor = MediaExtractor()
        videoExtractor.setDataSource(fd)
        audioExtractor.setDataSource(fd)
        val inputItem =
            createMediaSynthInputPart(basicInfo, videoExtractor, audioExtractor) ?: return null
        inputList.add(inputItem)
        return inputItem
    }

    fun addInput(path: String, headers: Map<String, String>?): ISynthInputPart? {
        val retriever = MediaMetadataRetriever()
        if (headers == null) {
            retriever.setDataSource(path)
        } else {
            retriever.setDataSource(path, headers)
        }
        val basicInfo = MediaInputBasicInfo(retriever)
        if (basicInfo.duration <= 0) {
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
        val inputItem =
            createMediaSynthInputPart(basicInfo, videoExtractor, audioExtractor) ?: return null
        inputList.add(inputItem)
        return inputItem
    }

    fun addInput(context: Context, uri: Uri, headers: Map<String, String>?): ISynthInputPart? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val basicInfo = MediaInputBasicInfo(retriever)
        if (basicInfo.duration <= 0) {
            return null
        }
        val videoExtractor = MediaExtractor()
        val audioExtractor = MediaExtractor()
        videoExtractor.setDataSource(context, uri, headers)
        audioExtractor.setDataSource(context, uri, headers)
        val inputItem =
            createMediaSynthInputPart(basicInfo, videoExtractor, audioExtractor) ?: return null
        inputList.add(inputItem)
        return inputItem
    }

    fun build(): T? {
        val writer = outputWriter
        val info = outputBasicInfo
        if (writer == null || info == null || inputList.isEmpty()) {
            return null
        }
        return createSynth(info, writer)
    }

    private fun createMediaSynthInputPart(
        basicInfo: MediaInputBasicInfo,
        videoExtractor: MediaExtractor,
        audioExtractor: MediaExtractor
    ): ISynthInputPart? {
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
        val videoTrackEditor = if (videoTrackIndex >= 0 && videoDecoderFormat != null) {
            createVideoTrackEditor(
                basicInfo, videoTrackIndex, videoDecoderFormat, videoExtractor
            )
        } else {
            null
        }
        val audioTrackEditor = if (audioTrackIndex >= 0 && audioDecoderFormat != null) {
            createAudioTrackEditor(
                basicInfo, audioTrackIndex, audioDecoderFormat, audioExtractor
            )
        } else {
            null
        }
        if (videoTrackEditor == null && audioTrackEditor == null) {
            return null
        }
        val editorList = ArrayList<ISynthTrackEditor>()
        if (videoTrackEditor != null) {
            editorList.add(videoTrackEditor)
        }
        if (audioTrackEditor != null) {
            editorList.add(audioTrackEditor)
        }
        return MediaSynthPartInfo(basicInfo, editorList)
    }

    protected open fun createVideoTrackEditor(
        basicInfo: MediaInputBasicInfo,
        trackIndex: Int,
        format: MediaFormat,
        extractor: MediaExtractor
    ): ISynthTrackEditor? {
        val originalMimeStr = format.getString(MediaFormat.KEY_MIME) ?: ""
        val inputFormat = extractor.getTrackFormat(trackIndex)
        val decoderMediaFormat =
            createVideoDecoderMediaFormat(basicInfo, originalMimeStr, inputFormat)
        val encoderMediaFormat =
            createVideoEncoderMediaFormat(basicInfo, originalMimeStr, inputFormat)
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
        val trackId = MediaSynthTrackId.VIDEO
        if (decoder == null && encoder == null) {
            return ExtractorEditor(trackId, trackIndex, decoderMediaFormat, extractor)
        }
        return CodecEditor(
            trackId, trackIndex, extractor, decoder, decoderMediaFormat, encoder, encoderMediaFormat
        )
    }

    protected open fun createAudioTrackEditor(
        basicInfo: MediaInputBasicInfo,
        trackIndex: Int,
        format: MediaFormat,
        extractor: MediaExtractor
    ): ISynthTrackEditor? {
        val originalMimeStr = format.getString(MediaFormat.KEY_MIME) ?: ""
        val decoderMediaFormat = createAudioDecoderMediaFormat(basicInfo, originalMimeStr, format)
        val encoderMediaFormat = createAudioEncoderMediaFormat(basicInfo, originalMimeStr, format)
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
        val trackId = MediaSynthTrackId.AUDIO
        if (decoder == null && encoder == null) {
            return ExtractorEditor(trackId, trackIndex, decoderMediaFormat, extractor)
        }
        return CodecEditor(
            trackId, trackIndex, extractor, decoder, decoderMediaFormat, encoder, encoderMediaFormat
        )
    }

    protected open fun createVideoDecoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        return null
    }

    protected open fun createVideoEncoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        return null
    }

    protected open fun createAudioDecoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        return null
    }

    protected open fun createAudioEncoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        return null
    }

    protected abstract fun createSynth(
        basicInfo: MediaOutputBasicInfo, writer: ISynthTrackWriter
    ): T?

}