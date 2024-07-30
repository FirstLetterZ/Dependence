package com.zpf.aaa.midea

import android.media.MediaMuxer
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicInteger

class TestMediaSynth(
    protected val inputs: LinkedList<MediaSynthInput>,
    protected val mediaMuxer: MediaMuxer
) {

    protected val statusCode = AtomicInteger(0)
    protected val workThreadLock = Object()
    protected val muxerLock = Object()



}