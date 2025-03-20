package  com.zpf.media.synth.i

import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

interface ISynthDispatcher {
    fun setTrackFormat(trackId: String, mediaFormat: MediaFormat?)
    fun writeTrackData(trackId: String, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo)
    fun updateTrackProgress(presentationTimeUs: Long, finished: Boolean)
}