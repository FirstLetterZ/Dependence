package com.zpf.aaa.synth

import android.media.MediaCodec

interface ISynthOutputListener {

    fun onDecoderOutput(
        index: Int,
        decoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        encoder: MediaCodec?,
    )

    fun onEncoderOutput(
        index: Int, encoder: MediaCodec, bufferInfo: MediaCodec.BufferInfo
    )

}
