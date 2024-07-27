package com.zpf.aaa.midea

import android.media.MediaExtractor
import java.util.concurrent.atomic.AtomicInteger

open class MediaTrackInfo(
    val sourceTrackIndex: Int,
    val extractor: MediaExtractor
) {
    val muxerTrackIndex = AtomicInteger(-1)
    val completed = AtomicInteger(0)
}