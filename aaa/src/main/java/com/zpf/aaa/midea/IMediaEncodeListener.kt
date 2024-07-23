package com.zpf.aaa.midea

import android.media.Image

interface IMediaEncodeListener {
    fun onEncode(image: Image, presentationTimeUs: Long)
}
