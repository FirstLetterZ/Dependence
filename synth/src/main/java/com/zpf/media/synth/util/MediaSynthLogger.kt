package com.zpf.media.synth.util

import android.util.Log

//todo zpf
object MediaSynthLogger {

    fun logInfo(message: String?) {
        if (message?.isNotEmpty() == true) {
            Log.i("ZPF", message)
        }
    }

    fun logError(message: String?) {
        if (message?.isNotEmpty() == true) {
            Log.e("ZPF", message)
        }
    }

}