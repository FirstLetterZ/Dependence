package com.zpf.aaa.videorope

import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.IOException


object FormatUtil {

    fun getVideoFormat(videoPath: String): MediaFormat? {
        val extractor = MediaExtractor()
        try {
            extractor.setDataSource(videoPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val trackCount = extractor.trackCount
        for (i in 0 until trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/")==true) {
                return format
            }
        }
        return null
    }
    fun areVideoFormatsCompatible(format1: MediaFormat, format2: MediaFormat): Boolean {
        // 比较 MIME 类型
        val mime1 = format1.getString(MediaFormat.KEY_MIME)
        val mime2 = format2.getString(MediaFormat.KEY_MIME)
        if (mime1 != mime2) {
            return false
        }

        // 比较分辨率
        val width1 = format1.getInteger(MediaFormat.KEY_WIDTH)
        val width2 = format2.getInteger(MediaFormat.KEY_WIDTH)
        if (width1 != width2) {
            return false
        }
        val height1 = format1.getInteger(MediaFormat.KEY_HEIGHT)
        val height2 = format2.getInteger(MediaFormat.KEY_HEIGHT)
        if (height1 != height2) {
            return false
        }

        // 比较帧率
        val frameRate1 = format1.getInteger(MediaFormat.KEY_FRAME_RATE)
        val frameRate2 = format2.getInteger(MediaFormat.KEY_FRAME_RATE)
        return if (frameRate1 != frameRate2) {
            false
        } else true

        // 比较其他参数（如需要）
        // ...
    }
}