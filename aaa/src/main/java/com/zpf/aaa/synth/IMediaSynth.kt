package  com.zpf.aaa.synth

import android.view.Surface

interface IMediaSynth {
    fun status(): Int
    fun start()
    fun pause()
    fun stop()
    fun reset()
    fun getDuration(): Long
    fun getInputInfo(i: Int): MediaSynthInput?
    fun setDecoderInputSurfaceChangedListener(listener: ISynthInputSurfaceListener?)
    fun setDecoderOutputSurface(surface: Surface?)
    fun getDecoderInputSurface(): Surface?
    fun setEncoderInputSurfaceChangedListener(listener: ISynthInputSurfaceListener?)
    fun setEncoderOutputSurface(surface: Surface?)
    fun getEncoderInputSurface(): Surface?
    fun setVideoListener(listener: ISynthOutputListener?)
    fun setAudioListener(listener: ISynthOutputListener?)
    fun addStatusListener(listener: ISynthStatusListener)
    fun removeStatusListener(listener: ISynthStatusListener)
    fun getOutputBasicInfo(): MediaOutputBasicInfo
}