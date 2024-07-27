package com.zpf.aaa.midea

interface ISynthProgressListener {
    fun onProgress(presentationTimeUs: Long, durationUs: Long, completed: Boolean)
}