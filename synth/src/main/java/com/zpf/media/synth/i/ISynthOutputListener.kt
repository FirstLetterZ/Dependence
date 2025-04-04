package  com.zpf.media.synth.i

import android.media.MediaCodec

interface ISynthOutputListener {

    fun onDecoderOutput(
        partIndex: Int,
        bufferIndex: Int,
        decoder: MediaCodec,
        bufferInfo: MediaCodec.BufferInfo,
        encoder: MediaCodec?,
    )

    fun onEncoderOutput(
        partIndex: Int, bufferIndex: Int, encoder: MediaCodec, bufferInfo: MediaCodec.BufferInfo
    )

}
