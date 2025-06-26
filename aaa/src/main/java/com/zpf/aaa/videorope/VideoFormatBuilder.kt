package com.zpf.aaa.videorope

import android.media.MediaFormat
import android.util.Log
import com.zpf.media.synth.AbsSynthBuilder
import com.zpf.media.synth.CoverMediaSynth
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaInputBasicInfo
import com.zpf.media.synth.model.MediaOutputBasicInfo

class VideoFormatBuilder : AbsSynthBuilder<CoverMediaSynth>() {
    override fun createSynth(
        basicInfo: MediaOutputBasicInfo, writer: ISynthTrackWriter
    ): CoverMediaSynth? {
//        return CoverMediaSynth(basicInfo, inputList, writer, coverFrameCount)
        return null
    }

    override fun createVideoDecoderMediaFormat(
        basicInfo: MediaInputBasicInfo,
        mimeStr: String,
        originalMediaFormat: MediaFormat
    ): MediaFormat? {
        Log.w("ZPF", "createVideoDecoderMediaFormat==>$originalMediaFormat")
        return super.createVideoDecoderMediaFormat(basicInfo, mimeStr, originalMediaFormat)
    }

    override fun createAudioDecoderMediaFormat(
        basicInfo: MediaInputBasicInfo,
        mimeStr: String,
        originalMediaFormat: MediaFormat
    ): MediaFormat? {
        Log.w("ZPF", "createAudioDecoderMediaFormat==>$originalMediaFormat")

        return super.createAudioDecoderMediaFormat(basicInfo, mimeStr, originalMediaFormat)
    }

}