package com.zpf.aaa.midea

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.util.Log
import android.view.Surface
import java.nio.ByteBuffer
import java.util.LinkedList

class TestSynth(
    videoUnit: MediaUnit?,
    audioUnit: MediaUnit?,
    mediaMuxer: MediaMuxer,
    retriever: MediaMetadataRetriever,
    mediaInfo: MediaInfo
) : BaseSynth(videoUnit, audioUnit, mediaMuxer, retriever, mediaInfo) {

    var inputSurface: Surface? = null
        private set

    override fun onConfigured(unit: MediaUnit) {
        if (inputSurface == null) {
            inputSurface = unit.encoder.createInputSurface()
        }
        super.onConfigured(unit)
    }

    override fun onCodecAudio(decoder: MediaCodec, encoder: MediaCodec, extractor: MediaExtractor) {
        var outputInfo = MediaCodec.BufferInfo()
        val totalDurationUs = mediaInfo.duration * 1000L
        var finishAll = false //用于判断整个编解码过程是否结束
        var decodeInputDone = false
        var decodeOutputDone = false
        var encodeOutputDone = false
        var muxerTrackIndex = -1
//        var lastEncodeOutputTimeStamp: Long = -1
        while (!finishAll && status() == MediaSynthStatus.START) {
            if (!decodeInputDone) {
                val decodeInputIndex = decoder.dequeueInputBuffer(INPUT_TIMEOUT)
                if (decodeInputIndex >= 0) {
                    val inputBuffer: ByteBuffer? = decoder.getInputBuffer(decodeInputIndex)
                    if (inputBuffer != null) {
                        inputBuffer.clear()
                        val readSampleData = extractor.readSampleData(inputBuffer, 0)
                        if (readSampleData < 0) {
                            decoder.queueInputBuffer(
                                decodeInputIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            decodeInputDone = true
                        } else {
                            decoder.queueInputBuffer(
                                decodeInputIndex, 0, readSampleData, extractor.sampleTime, 0
                            )
                            extractor.advance()
                        }
                    }
                }
            }
            if (!decodeOutputDone) {
                val decodeOutputIndex = decoder.dequeueOutputBuffer(outputInfo, OUTPUT_TIMEOUT)
                if (decodeOutputIndex >= 0) {
                    if (outputInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        decodeOutputDone = true
                        val encodeInputIndex = encoder.dequeueInputBuffer(INPUT_TIMEOUT)
                        encoder.queueInputBuffer(
                            encodeInputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                    } else if (outputInfo.size > 0) {
                        val decodeOutputBuffer = decoder.getOutputBuffer(decodeOutputIndex)
                        val encodeInputIndex = encoder.dequeueInputBuffer(INPUT_TIMEOUT)
                        val encodeInputBuffer: ByteBuffer? = if (encodeInputIndex >= 0) {
                            encoder.getInputBuffer(encodeInputIndex)
                        } else {
                            null
                        }
                        if (decodeOutputBuffer != null && encodeInputBuffer != null) {
                            val currentTimeUs = outputInfo.presentationTimeUs
                            encodeInputBuffer.clear()
                            if (outputInfo.size < 4096) {//这里看起来应该是16位单声道转16位双声道
                                val chunkPCM = ByteArray(outputInfo.size)
                                decodeOutputBuffer.get(chunkPCM)
                                decodeOutputBuffer.clear()
                                //说明是单声道的,需要转换一下
                                val stereoBytes = ByteArray(outputInfo.size * 2)
                                var i = 0
                                while (i < outputInfo.size) {
                                    stereoBytes[i * 2 + 0] = chunkPCM[i]
                                    stereoBytes[i * 2 + 1] = chunkPCM[i + 1]
                                    stereoBytes[i * 2 + 2] = chunkPCM[i]
                                    stereoBytes[i * 2 + 3] = chunkPCM[i + 1]
                                    i += 2
                                }
                                encodeInputBuffer.put(stereoBytes)
                                encoder.queueInputBuffer(
                                    encodeInputIndex,
                                    0,
                                    stereoBytes.size, currentTimeUs, 0
                                )
                            } else {
                                encodeInputBuffer.put(decodeOutputBuffer)
                                encoder.queueInputBuffer(
                                    encodeInputIndex,
                                    outputInfo.offset,
                                    outputInfo.size,
                                    currentTimeUs,
                                    0
                                )
                            }
                        }
                    }
                }
            }
            var encoderOutputAvailable = true
            var n = 0
            while (encoderOutputAvailable && status() == MediaSynthStatus.START && n < 10) {
                n++
                outputInfo = MediaCodec.BufferInfo()
                val encodeOutputIndex = encoder.dequeueOutputBuffer(outputInfo, OUTPUT_TIMEOUT)
                if (encodeOutputIndex >= 0) {
                    if (outputInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        encoderOutputAvailable = false
                        finishAll = true
                    }
                    val encodeOutputBuffer = encoder.getOutputBuffer(encodeOutputIndex)
                    if (outputInfo.size > 0 && encodeOutputBuffer != null) {
                        writeMuxerData(muxerTrackIndex, encodeOutputBuffer, outputInfo)
                    }
                    encoder.releaseOutputBuffer(encodeOutputIndex, false)
                } else if (encodeOutputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    muxerTrackIndex = onAudioEncodeFormatChanged(encoder.outputFormat)
                }
            }
        }
    }

    override fun onCodecVideo(decoder: MediaCodec, encoder: MediaCodec, extractor: MediaExtractor) {
        val statTime = System.currentTimeMillis()
        var finishAll = false
        var inputDone = false
        var outDone = false
        var muxerIndex = -1
        var outputInfo = MediaCodec.BufferInfo()
        val totalDurationUs = mediaInfo.duration * 1000L
        var totalDecodeSize: Long = 0L
        var totalEncodeSize: Long = 0L
        val list = LinkedList<ByteArray>()
        while (!finishAll && status() == MediaSynthStatus.START) {
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
                        if (readSampleData < 0) {
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
            if (status() != MediaSynthStatus.START) {
                return
            }
//            var yuvBytes: ByteArray? = null
            if (!outDone) {
                val outputIndex = decoder.dequeueOutputBuffer(outputInfo, OUTPUT_TIMEOUT)
                Log.e(
                    "ZPF",
                    "startVideoCodec==>>output 11111;outputIndex=$outputIndex;size=${outputInfo.size}"
                )

                if (outputIndex >= 0) {
                    if (outputInfo.size > 0) {
                        totalDecodeSize += outputInfo.size

                        val image = decoder.getOutputImage(outputIndex)
                        if (image != null) {
//                            val yuvBytes = Util.toNV21Bytes(image)
//                            list.add(yuvBytes)
                            outputListener?.onEncode(image, outputInfo.presentationTimeUs)
                            image.close()
//                            outputInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                        }
                        decoder.releaseOutputBuffer(outputIndex, true)

                    }
                    if (outputInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        encoder.signalEndOfInputStream()
                        outDone = true
                    }
                }
            }
//            val index2 = encoder.dequeueInputBuffer(INPUT_TIMEOUT)
//            if (index2 >= 0) {
//                val yuvBytes = list.pollFirst()
//                val inputBuffer = encoder.getInputBuffer(index2)
//                if (inputBuffer != null && yuvBytes != null) {
//                    inputBuffer.clear()
//                    inputBuffer.put(yuvBytes)
//                    encoder.queueInputBuffer(
//                        index2, 0, yuvBytes.size, outputInfo.presentationTimeUs, 0
//                    )
//                }
//            }
            var enableMuxer = true
            var n = 0
            val currentTimeUs = outputInfo.presentationTimeUs
            while (enableMuxer && status() == MediaSynthStatus.START && n < 10) {
                n++
                outputInfo = MediaCodec.BufferInfo()
                val index = encoder.dequeueOutputBuffer(outputInfo, MUXER_TIMEOUT)
                Log.e(
                    "ZPF",
                    "enableMuxer 111=presentationTimeUs= ${outputInfo.presentationTimeUs}; offset=${outputInfo.offset} ; size=${outputInfo.size};index=$index"
                )
                if (index >= 0) {
                    val outBuffer: ByteBuffer? = encoder.getOutputBuffer(index)
//                    if ((outputInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
//                        outputInfo.size = 0
//                    }
                    Log.e(
                        "ZPF",
                        "enableMuxer 222=${outputInfo.presentationTimeUs}; offset=${outputInfo.offset} ; size=${outputInfo.size};flag=${outputInfo.flags}"
                    )
                    totalEncodeSize += outputInfo.size
                    if (outBuffer != null && outputInfo.size != 0) {
                        if (currentTimeUs > 0) {
                            outputInfo.presentationTimeUs = currentTimeUs
                        }
                        outputInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                        outBuffer.position(outputInfo.offset)
                        outBuffer.limit(outputInfo.offset + outputInfo.size)
                        writeMuxerData(muxerIndex, outBuffer, outputInfo)
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
                    muxerIndex = onVideoEncodeFormatChanged(encoder.outputFormat)
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
            progressListener?.onProgress(
                totalDurationUs, totalDurationUs, true
            )
        }
        if (status() == MediaSynthStatus.RELEASE) {
            onReleased(MediaSynthStatus.START)
        } else if (finishAll) {
            release()
        }
    }


    protected fun onEncodeOutput(outputInfo: ByteBuffer) {

    }

}