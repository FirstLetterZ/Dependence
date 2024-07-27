package com.zpf.aaa.midea

import android.media.MediaCodec

interface ISynthCodecListener {

    fun onDecoderOutput(
        synth: IMediaSynth,
        decoderIndex: Int,
        decoder: MediaCodec,
        encoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo
    )

    fun onEncoderOutput(
        synth: IMediaSynth,
        encoderIndex: Int,
        encoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo
    )

}
