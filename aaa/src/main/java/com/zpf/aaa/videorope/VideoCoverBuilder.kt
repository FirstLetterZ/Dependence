package com.zpf.aaa.videorope

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import com.zpf.media.synth.AbsSynthBuilder
import com.zpf.media.synth.CodecEditor
import com.zpf.media.synth.CoverMediaSynth
import com.zpf.media.synth.ExtractorEditor
import com.zpf.media.synth.MediaSynthMuxerWriter
import com.zpf.media.synth.i.ISynthTrackEditor
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaInputBasicInfo
import com.zpf.media.synth.model.MediaOutputBasicInfo
import com.zpf.media.synth.model.MediaSynthPartInfo
import com.zpf.media.synth.model.MediaSynthTrackId

class VideoCoverBuilder(outputFilePath: String, private val coverFrameCount: Int = 30) :
    AbsSynthBuilder<CoverMediaSynth>() {
    init {
        outputWriter =
            MediaSynthMuxerWriter(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    private val frameRate = 30
    private var videoInput: ISynthTrackEditor? = null
    private var audioInput: ISynthTrackEditor? = null
    private var inputBasicInfo: MediaInputBasicInfo? = null

    override fun createSynth(
        basicInfo: MediaOutputBasicInfo, writer: ISynthTrackWriter
    ): CoverMediaSynth? {
        val inputInfo = inputBasicInfo ?: return null
        val video = videoInput ?: return null
        val audio = audioInput
        val editorList = ArrayList<ISynthTrackEditor>()
        editorList.add(video)
        if (audio != null) {
            editorList.add(audio)
        }
        val coverConfig = MediaSynthPartInfo(inputInfo, editorList)
        inputList.add(0, coverConfig)
        return CoverMediaSynth(basicInfo, inputList, writer, coverFrameCount)
    }

    override fun createAudioTrackEditor(
        basicInfo: MediaInputBasicInfo,
        trackIndex: Int,
        format: MediaFormat,
        extractor: MediaExtractor
    ): ISynthTrackEditor? {
        audioInput = ExtractorEditor(MediaSynthTrackId.AUDIO, trackIndex, format, null)
        return super.createAudioTrackEditor(basicInfo, trackIndex, format, extractor)
    }

    override fun createVideoEncoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
//        val frameRate = originalMediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
        val mediaFormat = MediaFormat.createVideoFormat(mimeStr, basicInfo.width, basicInfo.height)
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(
            MediaFormat.KEY_BIT_RATE, basicInfo.bitRate
        )
//        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE,3000000)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        val d = 1000L / frameRate * coverFrameCount
        inputBasicInfo = basicInfo.copy(duration = d)
        videoInput = CodecEditor(
            MediaSynthTrackId.VIDEO,
            0,
            null,
            null,
            null,
            MediaCodec.createEncoderByType(mimeStr),
            mediaFormat
        )
        outputBasicInfo = MediaOutputBasicInfo(
            basicInfo.fileMime, basicInfo.width, basicInfo.height, basicInfo.duration, frameRate
        )
        return null
    }
}