package com.zpf.aaa.midea

import android.view.Surface

interface IMediaSynth {
    fun status(): Int
    fun start()
    fun pause()
    fun stop()
    fun reset()
    fun getDecodeSurface(): Surface?
    fun getEncodeSurface(): Surface?
    fun getInputInfo(i: Int): MediaSynthInput?
    fun setVideoListener(listener: ISynthCodecListener?)
    fun setAudioListener(listener: ISynthCodecListener?)
    fun setTimeListener(listener: ISynthProgressListener?)
}