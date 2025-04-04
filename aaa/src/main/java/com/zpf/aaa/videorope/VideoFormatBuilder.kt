package com.zpf.aaa.videorope

import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import com.zpf.media.synth.AbsSynthBuilder
import com.zpf.media.synth.CoverMediaSynth
import com.zpf.media.synth.i.ISynthTrackEditor
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaInputBasicInfo
import com.zpf.media.synth.model.MediaOutputBasicInfo

class VideoFormatBuilder(outputFilePath: String, private val coverFrameCount: Int = 30) :
    AbsSynthBuilder<CoverMediaSynth>() {
//
    override fun createSynth(
        basicInfo: MediaOutputBasicInfo, writer: ISynthTrackWriter
    ): CoverMediaSynth? {
//        return CoverMediaSynth(basicInfo, inputList, writer, coverFrameCount)
        return null
    }

    override fun createAudioTrackEditor(
        basicInfo: MediaInputBasicInfo,
        trackIndex: Int,
        format: MediaFormat,
        extractor: MediaExtractor
    ): ISynthTrackEditor? {
//        audioInput = ExtractorEditor(MediaSynthTrackId.AUDIO, trackIndex, format, null)
        return super.createAudioTrackEditor(basicInfo, trackIndex, format, extractor)
    }

    override fun createVideoEncoderMediaFormat(
        basicInfo: MediaInputBasicInfo, mimeStr: String, originalMediaFormat: MediaFormat
    ): MediaFormat? {
        Log.w("ZPF", "VideoFormatBuilder originalMediaFormat==>$originalMediaFormat")
//        Log.w("ZPF", "createVideoEncoderMediaFormat 2==>$basicInfo")
        return null
    }
}