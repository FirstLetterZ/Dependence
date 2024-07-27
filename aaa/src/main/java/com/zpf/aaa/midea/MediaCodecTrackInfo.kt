package com.zpf.aaa.midea

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.util.concurrent.atomic.AtomicBoolean

open class MediaCodecTrackInfo(
    sourceIndex: Int,
    extractor: MediaExtractor,
    val decodeFormat: MediaFormat,
    val encodeFormat: MediaFormat,
    val decoder: MediaCodec,
    val encoder: MediaCodec,
) : MediaTrackInfo(sourceIndex, extractor) {
    val coderStarted = AtomicBoolean(false)
}