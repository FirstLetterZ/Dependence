package com.zpf.aaa.synth

import android.view.Surface

interface ISynthInputSurfaceListener {
    fun onDecoderInputSurfaceCreated(surface: Surface?)
    fun onEncoderInputSurfaceCreated(surface: Surface?)
}