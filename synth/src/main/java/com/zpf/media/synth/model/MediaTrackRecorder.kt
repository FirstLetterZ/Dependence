package  com.zpf.media.synth.model

import com.zpf.media.synth.i.ISynthOutputListener
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class MediaTrackRecorder(val trackId: String) {
    @Volatile
    var thread: Thread? = null
    var outputListener: ISynthOutputListener? = null
    val trackProgressTime = AtomicLong(Long.MAX_VALUE)
    val trackPartIndex = AtomicInteger(0)

    fun reset() {
        trackProgressTime.set(Long.MAX_VALUE)
        trackPartIndex.set(0)
    }
}