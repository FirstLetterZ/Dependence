package  com.zpf.media.synth.i

import android.media.MediaFormat

interface ISynthTrackEditor {
    fun trackId(): String
    fun isValid(): Boolean
    fun getInputFormat(): MediaFormat?
    fun isRunning(): Boolean
    fun start()
    fun stop()
}