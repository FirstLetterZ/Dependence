package com.zpf.aaa.synth

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class MediaTrackRecorder {
    var outputListener: ISynthOutputListener? = null
    val trackProgressTime = AtomicLong(Long.MAX_VALUE)
    val trackInputIndex = AtomicInteger(0)
    fun reset() {
        trackProgressTime.set(Long.MAX_VALUE)
        trackInputIndex.set(0)
    }
}