package com.zpf.aaa.synth

import android.media.MediaCodec

interface ISynthOutputListener {

    fun onDecoderOutput(
        mediaInfo: MediaInfo,
        index: Int,
        decoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        encoder: MediaCodec?,
    )

    fun onEncoderOutput(
        mediaInfo: MediaInfo, index: Int, encoder: MediaCodec, bufferInfo: MediaCodec.BufferInfo
    )

}
