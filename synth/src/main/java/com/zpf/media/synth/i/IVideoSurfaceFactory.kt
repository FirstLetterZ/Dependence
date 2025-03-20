package com.zpf.media.synth.i

import android.view.Surface
import com.zpf.media.synth.model.MediaInputBasicInfo

interface IVideoSurfaceFactory {
    fun create(index: Int, basicInfo: MediaInputBasicInfo): Surface?
}