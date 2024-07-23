package com.zpf.aaa.midea

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat

data class MediaUnit(
    val trackIndex: Int,
    val decodeFormat: MediaFormat,
    val encodeFormat: MediaFormat,
    val decoder: MediaCodec,
    val encoder: MediaCodec,
    val extractor: MediaExtractor,
)