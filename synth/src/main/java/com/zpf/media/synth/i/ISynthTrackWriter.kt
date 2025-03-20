package  com.zpf.media.synth.i

import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

interface ISynthTrackWriter {
    fun setFormat(trackId: String, mediaFormat: MediaFormat?): Int
    fun write(trackId: String, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo)
    fun isFormatted(trackId: String): Boolean
    fun start()
    fun stop()
}
