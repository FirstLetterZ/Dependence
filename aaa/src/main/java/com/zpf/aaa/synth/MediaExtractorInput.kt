package com.zpf.aaa.synth

import android.media.MediaExtractor
import java.util.concurrent.atomic.AtomicBoolean

open class MediaExtractorInput(
    val trackIndex: Int, val extractor: MediaExtractor?
) : IMediaSynthTrackInput {
    val isStarted = AtomicBoolean(false)
    override fun hasInputConfig(): Boolean {
        return extractor != null
    }

    override fun isRunning(): Boolean {
        return isStarted.get()
    }

    override fun start() {
        if (isStarted.get()) {
            return
        }
        isStarted.set(true)
        extractor?.selectTrack(trackIndex)
     }

    override fun stop() {
        if (!isStarted.get()) {
            return
        }
        isStarted.set(false)
        extractor?.release()
    }

}