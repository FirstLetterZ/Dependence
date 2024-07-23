package com.zpf.aaa.midea

interface IMediaSynthListener {
    fun onProgress(presentationTimeUs: Long, durationUs: Long, completed: Boolean)
}