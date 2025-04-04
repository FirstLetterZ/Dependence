package  com.zpf.media.synth.i

import android.view.Surface
import com.zpf.media.synth.model.MediaOutputBasicInfo

interface IMediaSynth {
    fun status(): Int
    fun start()
    fun pause()
    fun stop()
    /* fun reset() */
    fun takeTime(): Long
    fun getPartStartTime(partIndex: Int): Long
    fun addStatusListener(listener: ISynthStatusListener)
    fun removeStatusListener(listener: ISynthStatusListener)
    fun getOutputBasicInfo(): MediaOutputBasicInfo
    fun setTackOutputListener(trackId: String, listener: ISynthOutputListener?)
    fun setSynthSurfaceManager(manager: ISynthSurfaceManager?)
    fun getDecoderInputSurface(): Surface?
    fun getEncoderInputSurface(): Surface?
}