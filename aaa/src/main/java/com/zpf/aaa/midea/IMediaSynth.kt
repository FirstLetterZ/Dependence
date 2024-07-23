package com.zpf.aaa.midea

import android.view.Surface
import java.util.concurrent.Executor

interface IMediaSynth {
//    fun setOutputSurface(decodeSurface: Surface?, encodeSurface: Surface?)
    fun status(): Int
    fun start(executor: Executor)
    fun pause()
    fun stop()
    fun release()
}