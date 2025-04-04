package  com.zpf.media.synth.i

import android.view.Surface

interface ISynthSurfaceManager {
    fun onDecoderInputSurfaceCreated(partIndex: Int, surface: Surface)
    fun getDecoderOutputSurface(partIndex: Int): Surface?
    fun onEncoderInputSurfaceCreated(partIndex: Int, surface: Surface)
    fun getEncoderOutputSurface(partIndex: Int): Surface?
}