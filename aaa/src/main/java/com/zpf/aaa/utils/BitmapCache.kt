package com.zpf.aaa.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import android.view.View
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class BitmapCache(private val width: Int, private val height: Int) {
    private val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    private val canvas: Canvas = Canvas(bitmap)
    private val outStream: ByteArrayOutputStream = ByteArrayOutputStream(width * height)
//    private val buffer = ByteBuffer.allocate(outStream.size())

    fun checkSize(width: Int, height: Int): Boolean {
        return this.width == width && this.height == height
    }

    fun drawYuvImage(yuvImage: YuvImage): Boolean {
        val imageWidth = yuvImage.width
        val imageHeight = yuvImage.height
        if (width != imageWidth || height != imageHeight) {
            return false
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
//        buffer.clear()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, outStream)
        val newBitmap = BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size())
//        Log.e(
//            "ZPF",
//            "drawYuvImage==>${outStream.size()};${buffer.remaining()};${bitmap.rowBytes};${bitmap.getByteCount()}"
//        )
//        buffer.put(outStream.toByteArray())
        canvas.drawBitmap(newBitmap, 0f, 0f, null)

        return true
    }

    fun drawView(view: View) {
        view.draw(canvas)
    }

    fun drawCanvas(canvas: Canvas) {
        bitmap.compress(Bitmap.CompressFormat.JPEG,75,outStream)
        val newBitmap = BitmapFactory.decodeByteArray(outStream.toByteArray(), 0, outStream.size())

        canvas.drawBitmap(newBitmap, 0f, 0f, null)
    }
//    fun getBitmap(): Bitmap {
//        bitmap.compress()
//    }

}