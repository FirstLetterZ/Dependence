package com.zpf.aaa.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.media.MediaCodecInfo
import android.media.MediaCodecInfo.CodecCapabilities
import android.media.MediaCodecList
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.math.min


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

    fun covertToBitmap(image: Image): Bitmap {
        val rect = image.getCropRect()
        val yuvImage = YuvImage(
            toNV21Bytes(image), ImageFormat.NV21, rect.width(), rect.height(), null
        )
        val outStream = ByteArrayOutputStream(rect.width() * rect.height())
        yuvImage.compressToJpeg(rect, 75, outStream)
        return BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size())
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


    fun copyFromYuvImage(bitmap: Bitmap, yuvImage: YuvImage) {
        val outStream = ByteArrayOutputStream(yuvImage.width * yuvImage.height)
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, outStream)
        val src = ByteBuffer.wrap(outStream.toByteArray())
        bitmap.copyPixelsFromBuffer(src)
    }

    fun covert2Bitmap(yuvImage: YuvImage): Bitmap {
        val outStream = ByteArrayOutputStream(yuvImage.width * yuvImage.height)
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, outStream)
        return BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size())
    }

    fun covert2YuvImage(image: Image, rotate: Int): YuvImage {
        val rect = image.getCropRect()
        val width = rect.width()
        val height = rect.height()

//        return  YuvImage(
//            toNV21Bytes(image), ImageFormat.NV21, width, height, null
//        )
        return if (rotate == 0 || rotate == 180) {
            YuvImage(
                toNV21Bytes(image), ImageFormat.NV21, width, height, null
            )
        } else {
            val inputBytes = toNV21Bytes(image)
//                        val outBytes=ByteArray(inputBytes.size)
//            YuvUtil.NV21ToNV12(inputBytes,outBytes,width,height)
//            YuvUtil.rotateNV21(inputBytes,outBytes, width, height,rotate)
//            YuvImage(outBytes,  ImageFormat.NV21,  height,  width,null )
//            YuvImage(rotateYUV420Degree90(outBytes,width,height),  ImageFormat.NV21,  height,  width,null )
            YuvImage(
                rotateNv21Degree90(inputBytes, width, height),
                ImageFormat.NV21,
                height,
                width,
                null
            )
        }
    }

    fun rotateNv21Degree90(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
        val yuv = ByteArray(imageWidth * imageHeight * 3 / 2)
        // Rotate the Y luma
        var i = 0
        for (x in 0 until imageWidth) {
            for (y in imageHeight - 1 downTo 0) {
                yuv[i] = data[y * imageWidth + x]
                i++
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1
        var x = imageWidth - 1
        while (x > 0) {
            for (y in 0 until imageHeight / 2) {
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + x]
                i--
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + (x - 1)]
                i--
            }
            x -= 2
        }
        return yuv
    }


    fun rotate90(nv21Data: ByteArray, width: Int, height: Int): ByteArray {
        val rotatedWidth = height
        val rotatedHeight = width
        val rotatedNv21Data = ByteArray(nv21Data.size)

        for (y in 0 until height) {
            for (x in 0 until width) {
                rotatedNv21Data[(rotatedWidth - 1 - x) * rotatedHeight + y] =
                    nv21Data[y * width + x];
            }
        }

        val uvHeight = height / 2
        val uvWidth = width / 2
        val uvOffset = width * height

        for (y in 0 until uvHeight) {
            for (x in 0 until uvWidth) {
                val uIndex = uvOffset + y * width + x * 2
                val vIndex = uIndex + 1
                val rotatedIndex = (rotatedWidth - 1 - x * 2) * rotatedHeight + y * 2;

                rotatedNv21Data[rotatedIndex] = nv21Data[uIndex]
                rotatedNv21Data[rotatedIndex + 1] = nv21Data[vIndex]
            }
        }
        return rotatedNv21Data
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


    fun getSupportColorFormat(): Int {
        val numCodecs = MediaCodecList.getCodecCount()
        var codecInfo: MediaCodecInfo? = null
        run {
            var i = 0
            while (i < numCodecs && codecInfo == null) {
                val info = MediaCodecList.getCodecInfoAt(i)
                if (!info.isEncoder) {
                    i++
                    continue
                }
                val types = info.getSupportedTypes()
                var found = false
                var j = 0
                while (j < types.size && !found) {
                    if (types[j] == "video/avc") {
                        Log.d("TAG:", "found")
                        found = true
                    }
                    j++
                }
                if (!found) {
                    i++
                    continue
                }
                codecInfo = info
                i++
            }
        }
        Log.e("TAG", "Found " + codecInfo!!.name + " supporting " + "video/avc")
        // Find a color profile that the codec supports
        val capabilities = codecInfo!!.getCapabilitiesForType("video/avc")
        Log.e(
            "TAG",
            "length-" + capabilities.colorFormats.size + "==" + capabilities.colorFormats.contentToString()
        )
        for (i in capabilities.colorFormats.indices) {
            Log.d("ZPF", "TAG MediaCodecInfo COLOR FORMAT :" + capabilities.colorFormats[i])
            if (capabilities.colorFormats[i] == CodecCapabilities.COLOR_FormatYUV420SemiPlanar || capabilities.colorFormats[i] == CodecCapabilities.COLOR_FormatYUV420Planar) {
                return capabilities.colorFormats[i]
            }
        }
        return CodecCapabilities.COLOR_FormatYUV420Flexible
    }


}