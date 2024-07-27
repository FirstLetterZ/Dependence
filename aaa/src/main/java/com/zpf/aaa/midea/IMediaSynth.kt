package com.zpf.aaa.midea

import android.media.MediaMetadataRetriever
import android.view.Surface

interface IMediaSynth {
    fun status(): Int
    fun start()
    fun pause()
    fun stop()
    fun reset()
    fun getInputSurface(): Surface?
    fun setVideoListener(listener: ISynthCodecListener?)
    fun setAudioListener(listener: ISynthCodecListener?)
    fun setTimeListener(listener: ISynthProgressListener?)
    val retriever: MediaMetadataRetriever
    val mediaInfo: MediaInfo

}