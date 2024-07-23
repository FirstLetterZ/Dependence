package com.zpf.aaa.midea

fun interface ProgressListener {
    fun onProgress(presentationTimeUs: Long, durationUs: Long)
}