package com.zpf.aaa.midea

import android.media.MediaExtractor
import android.media.MediaMetadataRetriever
import java.util.concurrent.atomic.AtomicInteger

data class MediaSynthInput(
    val videoTrackIndex: Int,
    val videoExtractor: MediaExtractor?,
    val audioTrackIndex: Int,
    val audioExtractor: MediaExtractor?,
    val retriever: MediaMetadataRetriever,
    val mediaInfo: MediaInfo,
    val status: AtomicInteger = AtomicInteger(0)
)