package  com.zpf.media.synth.i

import android.view.Surface
import com.zpf.media.synth.model.MediaOutputBasicInfo

interface IMediaSynth {
    fun status(): Int
    fun start()
    fun pause()
    fun stop()
//    fun reset()
    fun takeTime(): Long
    fun addStatusListener(listener: ISynthStatusListener)
    fun removeStatusListener(listener: ISynthStatusListener)
    fun getOutputBasicInfo(): MediaOutputBasicInfo
    fun setTackOutputListener(trackId: String, listener: ISynthOutputListener?)
    fun setDecoderInputSurfaceChangedListener(listener: ISynthSurfaceListener?)
    fun getDecoderInputSurface(): Surface?
    fun setDecoderOutputSurface(surface: Surface?)
    fun setEncoderInputSurfaceChangedListener(listener: ISynthSurfaceListener?)
    fun getEncoderInputSurface(): Surface?
    fun setEncoderOutputSurface(surface: Surface?)
}