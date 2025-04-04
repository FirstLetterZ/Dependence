package com.zpf.aaa.videorope

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.util.Log
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
import kotlin.math.min

class VideoCoverBuilder2(outputFilePath: String, private val coverFrameCount: Int = 30) :
    AbsSynthBuilder<CoverMediaSynth>() {
    init {
        outputWriter =
            MediaSynthMuxerWriter(outputFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    private val maxFrameRate = 30
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

//    override fun createVideoDecoderMediaFormat(
//        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
//    ): MediaFormat {
//        return originalMediaFormat
//    }

    override fun createVideoEncoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        val displayWidth = basicInfo.getTrueWidth()
        val displayHeight = basicInfo.getTrueHeight()
        val frameRate =
            min(maxFrameRate, originalMediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE))
        val coverFormat = MediaFormat.createVideoFormat(mimeStr, displayWidth, displayHeight)
        coverFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        val profile = try {
            originalMediaFormat.getInteger(MediaFormat.KEY_PROFILE)
        } catch (e: Exception) {
            1
        }
        coverFormat.setInteger(MediaFormat.KEY_PROFILE, profile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val level = try {
                originalMediaFormat.getInteger(MediaFormat.KEY_LEVEL)
            } catch (e: Exception) {
                1
            }
            coverFormat.setInteger(MediaFormat.KEY_LEVEL, level)
        }
        coverFormat.setInteger(MediaFormat.KEY_BIT_RATE, basicInfo.bitRate)
        coverFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        coverFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        val d = 1000L * coverFrameCount / frameRate
        inputBasicInfo = basicInfo.copy(duration = d)
        videoInput = CodecEditor(
            MediaSynthTrackId.VIDEO,
            0,
            null,
            null,
            null,
            MediaCodec.createEncoderByType(mimeStr),
            coverFormat
        )
        outputBasicInfo = MediaOutputBasicInfo(
            basicInfo.fileMime, displayWidth, displayHeight, basicInfo.duration, frameRate
        )
        Log.w("ZPF", "createVideoEncoderMediaFormat outputBasicInfo==>$outputBasicInfo")

        val mediaFormat = MediaFormat.createVideoFormat(mimeStr, displayWidth, displayHeight)
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, basicInfo.bitRate)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
//        return mediaFormat
        return null
    }
}