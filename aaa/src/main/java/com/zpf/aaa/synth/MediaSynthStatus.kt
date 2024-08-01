package com.zpf.aaa.synth

object MediaSynthStatus {
    const val WRITE_ERROR = -6
    const val BUFFER_SIZE_ERROR = -5
    const val DECODER_ERROR = -4
    const val INDEX_NULL_ERROR = -3
    const val CONFIG_ERROR = -2
    const val ERROR = -1
    const val CREATE = 0
    const val START = 1
    const val PAUSE = 2
    const val STOP = 3
    const val COMPLETE = 4
}