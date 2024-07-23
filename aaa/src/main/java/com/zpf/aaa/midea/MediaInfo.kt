package com.zpf.aaa.midea

data class MediaInfo(
    val outputMime: String,
    val width: Int,
    val height: Int,
    val rotation: Int,
    val duration: Long,
) {
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