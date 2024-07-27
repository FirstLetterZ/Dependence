package com.zpf.aaa.tst

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.util.Log
import android.view.Surface
import com.zpf.aaa.midea.ISynthProgressListener
import com.zpf.aaa.midea.MediaInfo
import java.io.File
import java.nio.ByteBuffer
import java.util.LinkedList

class VideoMuxerTest(val inputFile: File, val outputFile: File) {
    val retriever: MediaMetadataRetriever = MediaMetadataRetriever()
    val mediaInfo: MediaInfo
//    var outputListener: ISynthEncodeListener? = null
    var progressListener: ISynthProgressListener? = null

    var inputSurface: Surface? = null

    //    val videoTrackIndex: Int
//    val videoMuxerIndex: Int
//    val videoDecodeFormat: MediaFormat
//    val videoEncodeFormat: MediaFormat
    val videoDecoder: MediaCodec = MediaCodec.createDecoderByType("video/avc")
    val videoEncoder: MediaCodec = MediaCodec.createEncoderByType("video/avc")
    val videoExtractor: MediaExtractor = MediaExtractor()
    val mediaMuxer: MediaMuxer
    private var muxerStarted = false
    protected val INPUT_TIMEOUT = 0L
    protected val OUTPUT_TIMEOUT = 0L
    protected val MUXER_TIMEOUT = 0L
    protected var firstEncodeFrameTime = 0L
    protected var firstMuxerFrameTime = 0L
    var muxerTrackIndex = 0

    init {
        retriever.setDataSource(inputFile.absolutePath)
        mediaInfo = initMediaInfo(retriever)

        var videoTrack = -1
        var videoDecoderFormat: MediaFormat? = null
        var audioTrack = -1
        var audioDecoderFormat: MediaFormat? = null
        videoExtractor.setDataSource(inputFile.absolutePath)
        var extractor = videoExtractor
        for (i in 0 until extractor.trackCount) {
            val trackFormat = extractor.getTrackFormat(i)
            val mine = trackFormat.getString(MediaFormat.KEY_MIME)
            Log.e("ZPF", "trackFormat=$trackFormat")
            if (mine?.startsWith("video/") == true) {
                videoTrack = i
                videoDecoderFormat = trackFormat
            } else if (mine?.startsWith("audio/") == true) {
                audioTrack = i
                audioDecoderFormat = trackFormat
            }
        }

        videoExtractor.selectTrack(videoTrack)
        mediaMuxer =
            MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        val encodeFormat= buildVideoEncoderMediaFormat(
            mediaInfo,
            "video/avc",
            videoDecoderFormat!!
        )
        Log.e("TEST","VideoMuxerTest==>")
        Log.e("TEST","trackIndex=${videoTrack};")
        Log.e("TEST","decodeFormat=${videoDecoderFormat};")
        Log.e("TEST","encodeFormat=${encodeFormat};")
        Log.e("TEST","outputFilePath=${outputFile.absolutePath};")
        Log.e("TEST","MediaMuxerFormat=${ MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4};")
        try {
            videoEncoder.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE  )
            videoDecoder.configure(videoDecoderFormat, null, null, 0)
            videoDecoder.setCallback(object : MediaCodec.Callback() {
                override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                    Log.e("ZPF", "=== videoDecoder onInputBufferAvailable ===")
                    val buffer = codec.getInputBuffer(index)
                    if (buffer != null) {
                        buffer.clear()
                        val readSampleData = extractor.readSampleData(buffer, 0)
                        if (readSampleData <= 0) {
                            codec.queueInputBuffer(
                                index, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                        } else {
                            codec.queueInputBuffer(
                                index, 0, readSampleData, extractor.sampleTime, 0
                            )
                            extractor.advance()
                        }
                    }
                }

                override fun onOutputBufferAvailable(
                    codec: MediaCodec,
                    index: Int,
                    info: MediaCodec.BufferInfo
                ) {
                    Log.e(
                        "ZPF",
                        "videoDecoder onOutputBufferAvailable ===>time=${info.presentationTimeUs};size=${info.size}"
                    )
//                    if (firstEncodeFrameTime == 0L) {
//                        firstEncodeFrameTime = info.presentationTimeUs
//                    }
                    val image = codec.getOutputImage(index)
                    if (image != null) {
//                            val yuvBytes = Util.toNV21Bytes(image)
//                            list.add(yuvBytes)
//                        outputListener?.onEncode(image,mediaInfo, info.presentationTimeUs)
                        image.close()
//                            outputInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                    }

                    codec.releaseOutputBuffer(index, false)
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        videoEncoder.signalEndOfInputStream()
                    }
                }


                override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                    Log.e("ZPF", "=== videoDecoder onError ===$e")


                }

                override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                    Log.e("ZPF", "=== videoDecoder onOutputFormatChanged ===")


                }
            })
            videoEncoder.setCallback(object : MediaCodec.Callback() {
                override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                    Log.e("ZPF", "=== videoEncoder onInputBufferAvailable ===")


                }

                override fun onOutputBufferAvailable(
                    codec: MediaCodec,
                    index: Int,
                    info: MediaCodec.BufferInfo
                ) {
                    Log.e(
                        "ZPF",
                        "videoEncoder onOutputBufferAvailable ===>time=${info.presentationTimeUs};size=${info.size}"
                    )
//                    if (firstMuxerFrameTime == 0L) {
//                        firstMuxerFrameTime = info.presentationTimeUs
//                    }
//                    Log.e(
//                        "ZPF",
//                        "firstEncodeFrameTime=${firstEncodeFrameTime};firstMuxerFrameTime=${firstMuxerFrameTime}"
//                    )

                    val currentTime =
                        info.presentationTimeUs - firstMuxerFrameTime + firstEncodeFrameTime
                    val outputBuffer = codec.getOutputBuffer(index)
                    if (outputBuffer != null && info.size != 0) {
                        info.presentationTimeUs = currentTime
                        outputBuffer.position(info.offset)
                        outputBuffer.limit(info.offset + info.size)
                        mediaMuxer.writeSampleData(muxerTrackIndex, outputBuffer, info)
                        progressListener?.onProgress(currentTime, mediaInfo.duration * 1000L, false)
                    }
                    codec.releaseOutputBuffer(index, true)
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        release()
                        progressListener?.onProgress(mediaInfo.duration * 1000L, mediaInfo.duration * 1000L, true)
                    }
                }

                override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
                    Log.e("ZPF", "=== videoEncoder onError ===$e")


                }

                override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                    Log.e("ZPF", "=== videoEncoder onOutputFormatChanged ===")

                    muxerTrackIndex = mediaMuxer.addTrack(format)
                    mediaMuxer.start()
                    muxerStarted = true
                }
            })
            inputSurface = videoEncoder.createInputSurface()
            Thread {
                Thread.sleep(200L)
                videoDecoder.start()
                videoEncoder.start()
//                onCodecVideo(videoDecoder, videoEncoder, videoExtractor)
            }.start()

        } catch (e: Exception) {
            Log.e("ZPF", "$e")
            e.printStackTrace()
        }
    }

    fun onCodecVideo(decoder: MediaCodec, encoder: MediaCodec, extractor: MediaExtractor) {
        val statTime = System.currentTimeMillis()
        var finishAll = false
        var inputDone = false
        var outDone = false
        var muxerIndex = -1
        var outputInfo: MediaCodec.BufferInfo
        val totalDurationUs = mediaInfo.duration * 1000L
        var totalDecodeSize: Long = 0L
        var totalEncodeSize: Long = 0L
        val list = LinkedList<ByteArray>()
        while (!finishAll && enableCodec()) {
            if (!inputDone) {
                val inputIndex = decoder.dequeueInputBuffer(INPUT_TIMEOUT)
                Log.e(
                    "ZPF", "startVideoCodec==>>decode input 11111;inputIndex=$inputIndex"
                )
                if (inputIndex >= 0) {
                    val inputBuffer: ByteBuffer? = decoder.getInputBuffer(inputIndex)
                    if (inputBuffer != null) {
                        inputBuffer.clear()
                        val readSampleData = extractor.readSampleData(inputBuffer, 0)
                        if (readSampleData <= 0) {
                            decoder.queueInputBuffer(
                                inputIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            inputDone = true
                        } else {
                            decoder.queueInputBuffer(
                                inputIndex, 0, readSampleData, extractor.sampleTime, 0
                            )
                            extractor.advance()
                        }
                    }
                }
            }
            if (!enableCodec()) {
                return
            }
//            var yuvBytes: ByteArray? = null
            if (!outDone) {
                outputInfo = MediaCodec.BufferInfo()
                val outputIndex = decoder.dequeueOutputBuffer(outputInfo, OUTPUT_TIMEOUT)
                Log.e(
                    "ZPF",
                    "startVideoCodec==>>output 11111;outputIndex=$outputIndex;size=${outputInfo.size}"
                )

                if (outputIndex >= 0) {
                    if (outputInfo.size != 0) {
                        totalDecodeSize += outputInfo.size
                        val image = decoder.getOutputImage(outputIndex)
                        if (image != null) {
//                            val yuvBytes = Util.toNV21Bytes(image)
//                            list.add(yuvBytes)
//                            outputListener?.onEncode(image,mediaInfo, outputInfo.presentationTimeUs)
                            image.close()
//                            outputInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                        }
                    }
                    decoder.releaseOutputBuffer(outputIndex, false)
                    if (outputInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        encoder.signalEndOfInputStream()
                        outDone = true
                    }
                }
            }
            var enableMuxer = true
            var n = 0
            while (enableMuxer && enableCodec() && n < 10) {
                n++
                outputInfo = MediaCodec.BufferInfo()
                val index = encoder.dequeueOutputBuffer(outputInfo, MUXER_TIMEOUT)
                val currentTimeUs = outputInfo.presentationTimeUs

                Log.e(
                    "ZPF",
                    "enableMuxer 111=presentationTimeUs= ${outputInfo.presentationTimeUs}; offset=${outputInfo.offset} ; size=${outputInfo.size};index=$index"
                )
                if (index >= 0) {
                    val outBuffer: ByteBuffer? = encoder.getOutputBuffer(index)
                    if ((outputInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        outputInfo.size = 0
                    }
                    Log.e(
                        "ZPF",
                        "enableMuxer 222=${outputInfo.presentationTimeUs}; offset=${outputInfo.offset} ; size=${outputInfo.size};flag=${outputInfo.flags}"
                    )
                    totalEncodeSize += outputInfo.size
                    if (outBuffer != null && outputInfo.size != 0) {
//                        if (currentTimeUs > 0) {
//                            outputInfo.presentationTimeUs = currentTimeUs
//                        }
//                        outputInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                        outBuffer.position(outputInfo.offset)
                        outBuffer.limit(outputInfo.offset + outputInfo.size)
                        mediaMuxer.writeSampleData(muxerIndex, outBuffer, outputInfo)
//                        writeMuxerData(muxerIndex, outBuffer, outputInfo)
                        progressListener?.onProgress(
                            currentTimeUs, totalDurationUs, false
                        )
                        Log.e("ZPF", "enableMuxer 333=${outputInfo.presentationTimeUs}")
                    } else {
                        enableMuxer = false
                    }
                    encoder.releaseOutputBuffer(index, false)
                    finishAll = outputInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                    if (finishAll) {
                        enableMuxer = false
                    }
                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    muxerIndex = mediaMuxer.addTrack(encoder.outputFormat)
                    mediaMuxer.start()
                    Log.e(
                        "ZPF", "INFO_OUTPUT_FORMAT_CHANGED"
                    )
//                    muxerIndex =
//                        muxerIndex = onVideoEncodeFormatChanged(encoder.outputFormat)
                } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    enableMuxer = false
                }
            }
        }

        Log.e(
            "ZPF",
            "takeTime=${System.currentTimeMillis() - statTime};totalDecodeSize=$totalDecodeSize;totalEncodeSize=$totalEncodeSize;finishAll=$finishAll"
        )

        if (finishAll) {

            videoDecoder.stop()
            videoDecoder.release()
            videoEncoder.stop()
            videoEncoder.release()
            videoExtractor.release()
            mediaMuxer.stop()
            mediaMuxer.release()

//            progressListener?.onProgress(
//                totalDurationUs, totalDurationUs, true
//            )
        }
//        if (status() == MediaSynthStatus.RELEASE) {
//            onReleased(MediaSynthStatus.START)
//        } else if (finishAll) {
//            release()
//        }
    }


    protected fun release() {
        Log.e("ZPF", "=== release ===")

        videoDecoder.stop()
        videoDecoder.release()
        videoEncoder.stop()
        videoEncoder.release()
        videoExtractor.release()
        if (muxerStarted) {
            mediaMuxer.stop()
            mediaMuxer.release()
        }
    }

    private fun initMediaInfo(mediaMetadataRetriever: MediaMetadataRetriever): MediaInfo {
        val mime =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                ?: ""
        val width =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                ?.toInt() ?: 0
        val height =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                ?.toInt() ?: 0
        val rotation =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                ?.toInt() ?: 0
        val duration =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLong() ?: 0L
        return MediaInfo(mime, width, height, rotation, duration)
    }

    protected fun enableCodec(): Boolean {
        return true
    }

    protected fun buildVideoEncoderMediaFormat(
        mediaInfo: MediaInfo, mime: String, originalMediaFormat: MediaFormat
    ): MediaFormat {
        val mediaFormat =
            MediaFormat.createVideoFormat(mime, mediaInfo.getTrueWidth(), mediaInfo.getTrueHeight())
//            MediaFormat.createVideoFormat(mime, mediaInfo.width, mediaInfo.height)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mediaFormat.setInteger(
//                MediaFormat.KEY_ROTATION, originalMediaFormat.getInteger(MediaFormat.KEY_ROTATION)
//            )
//        }
        mediaFormat.setInteger(
//            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
            MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        mediaFormat.setInteger(
//            MediaFormat.KEY_BIT_RATE, 3000000
            MediaFormat.KEY_BIT_RATE, mediaInfo.width * mediaInfo.height * 4
        )
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        return mediaFormat
    }


}