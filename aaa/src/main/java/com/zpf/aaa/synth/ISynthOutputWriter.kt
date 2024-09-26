package  com.zpf.aaa.synth

import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

interface ISynthOutputWriter {
    fun setFormat(trackId: Int, mediaFormat: MediaFormat?): Int
    fun write(trackId: Int, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo)
    fun isFormatted(trackId: Int): Boolean
    fun start()
    fun stop()
}
