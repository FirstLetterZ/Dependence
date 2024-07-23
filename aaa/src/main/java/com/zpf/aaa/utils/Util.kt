package com.zpf.aaa.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.YuvImage
import android.media.Image
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException


object Util {

    fun compressToJpeg(fileName: String, image: Image) {
        val outStream: FileOutputStream = try {
            FileOutputStream(fileName)
        } catch (ioe: IOException) {
            throw RuntimeException("Unable to create output file $fileName", ioe)
        }
        val rect = image.getCropRect()
        val yuvImage = YuvImage(
            toNV21Bytes(image), ImageFormat.NV21, rect.width(), rect.height(), null
        )
        yuvImage.compressToJpeg(rect, 100, outStream)
    }

    fun covertToBitmap( image: Image):Bitmap {
        val rect = image.getCropRect()
        val yuvImage = YuvImage(
            toNV21Bytes(image), ImageFormat.NV21, rect.width(), rect.height(), null
        )
        val outStream = ByteArrayOutputStream(rect.width() * rect.height())
        yuvImage.compressToJpeg(rect, 100, outStream)
       return BitmapFactory.decodeByteArray(outStream.toByteArray(),0,outStream.size())
    }


    fun compressToBitmap(bitmap: Bitmap, image: Image) {
        val rect = image.getCropRect()
        val yuvImage = YuvImage(
            toNV21Bytes(image), ImageFormat.NV21, rect.width(), rect.height(), null
        )
        val outStream = ByteArrayOutputStream(rect.width() * rect.height())
        yuvImage.compressToJpeg(rect, 100, outStream)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
    }

    fun toNV21Bytes(image: Image): ByteArray {
        val crop = image.getCropRect()
        val format = image.format
        val width = crop.width()
        val height = crop.height()
        val planes = image.planes
        val data = ByteArray(width * height * ImageFormat.getBitsPerPixel(format) / 8)
        val rowData = ByteArray(planes[0].rowStride)
        var channelOffset = 0
        var outputStride = 1
        for (i in planes.indices) {
            when (i) {
                0 -> {
                    channelOffset = 0
                    outputStride = 1
                }
                1 -> {
                    channelOffset = width * height + 1
                    outputStride = 2
                }
                2 -> {
                    channelOffset = width * height
                    outputStride = 2
                }
            }
            val buffer = planes[i].buffer
            val rowStride = planes[i].rowStride
            val pixelStride = planes[i].pixelStride

            val shift = if (i == 0) 0 else 1
            val w = width shr shift
            val h = height shr shift
            buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
            for (row in 0 until h) {
                var length: Int
                if (pixelStride == 1 && outputStride == 1) {
                    length = w
                    buffer[data, channelOffset, length]
                    channelOffset += length
                } else {
                    length = (w - 1) * pixelStride + 1
                    buffer[rowData, 0, length]
                    for (col in 0 until w) {
                        data[channelOffset] = rowData[col * pixelStride]
                        channelOffset += outputStride
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length)
                }
            }
        }
        return data
    }
}