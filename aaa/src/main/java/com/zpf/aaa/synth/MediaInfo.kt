package com.zpf.aaa.synth

import android.media.MediaMetadataRetriever

data class MediaInfo(
    val outputMime: String,
    val width: Int,
    val height: Int,
    val rotation: Int,
    val duration: Long,
) {

    constructor(mediaMetadataRetriever: MediaMetadataRetriever) : this(
        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: "",
        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            ?.toInt() ?: 0,
        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            ?.toInt() ?: 0,
        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            ?.toInt() ?: 0,
        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLong() ?: 0L
    )

    fun getTrueWidth(): Int {
        return if (rotation == 0 || rotation == 180) {
            width
        } else {
            height
        }
    }

    fun getTrueHeight(): Int {
        return if (rotation == 0 || rotation == 180) {
            height
        } else {
            width
        }
    }
}