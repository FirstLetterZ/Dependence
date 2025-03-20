package  com.zpf.media.synth

import android.media.MediaExtractor
import android.media.MediaFormat
import com.zpf.media.synth.i.ISynthTrackEditor
import java.util.concurrent.atomic.AtomicBoolean

open class ExtractorEditor(
    protected val trackId: String,
    val trackIndex: Int,
    val trackFormat: MediaFormat?,
    val extractor: MediaExtractor?
) : ISynthTrackEditor {
    val isStarted = AtomicBoolean(false)

    override fun trackId(): String {
        return trackId
    }

    override fun isValid(): Boolean {
        return trackIndex >= 0
    }

    override fun getInputFormat(): MediaFormat? {
        return trackFormat ?: extractor?.getTrackFormat(trackIndex)
    }

    override fun isRunning(): Boolean {
        return isStarted.get()
    }

    override fun start() {
        if (isRunning()) {
            return
        }
        isStarted.set(true)
        extractor?.selectTrack(trackIndex)
    }

    override fun stop() {
        if (!isRunning()) {
            return
        }
        isStarted.set(false)
        extractor?.unselectTrack(trackIndex)
        extractor?.release()
    }

}