package  com.zpf.media.synth.i

import android.media.MediaCodec

interface ISynthOutputListener {

    fun onDecoderOutput(
        bufferIndex: Int,
        decoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        encoder: MediaCodec?,
    )

    fun onEncoderOutput(
        bufferIndex: Int, encoder: MediaCodec, bufferInfo: MediaCodec.BufferInfo
    )

}
