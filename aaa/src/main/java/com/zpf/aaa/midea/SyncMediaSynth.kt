package com.zpf.aaa.midea

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import java.nio.ByteBuffer

class SyncMediaSynth(
    videoTrack: MediaCodecTrackInfo,
    audioTrack: MediaTrackInfo?,
    mediaMuxer: MediaMuxer,
    retriever: MediaMetadataRetriever,
    mediaInfo: MediaInfo
) : AbsMediaSynth(videoTrack, audioTrack, mediaMuxer, retriever, mediaInfo) {
    protected var INPUT_TIMEOUT = 10L
    protected var OUTPUT_TIMEOUT = 10L
    protected var MUXER_TIMEOUT = 10L
    private val frameRate: Int

    init {
        val videoFormat = videoTrack.encodeFormat
        val rate = videoFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
        frameRate = if (rate > 0) {
            rate
        } else {
            30
        }
    }

    override fun configureAudioTrack(trackInfo: MediaTrackInfo) {
        super.configureAudioTrack(trackInfo)
        val mi = mediaMuxer.addTrack(trackInfo.extractor.getTrackFormat(trackInfo.sourceTrackIndex))
        trackInfo.muxerTrackIndex.set(mi)
    }

    override fun startAudioTrack(trackInfo: MediaTrackInfo) {
        synchronized(threadLock) {
            if (audioThread?.isAlive != true) {
                val thread = Thread {
                    val res = copyMedia(trackInfo) {
                        onAudioProgressUpdate(it)
                    }
                    audioThread = null
                    if (res) {
                        trackInfo.completed.set(1)
                        checkFinish()
                    }
                }
                thread.start()
                audioThread = thread
            } else {
                threadLock.notifyAll()
            }
        }
    }

    override fun startVideoTrack(trackInfo: MediaTrackInfo) {
        val unit = trackInfo as? MediaCodecTrackInfo ?: return
        synchronized(threadLock) {
            if (!trackInfo.coderStarted.get()) {
                trackInfo.coderStarted.set(true)
                unit.decoder.start()
                unit.encoder.start()
            }
            if (videoThread?.isAlive != true) {
                val thread = Thread {
                    val res = codecVideo(unit)
                    videoThread = null
                    if (res) {
                        trackInfo.completed.set(1)
                        checkFinish()
                    }
                }
                thread.start()
                videoThread = thread
            } else {
                threadLock.notifyAll()
            }
        }
    }


    protected fun codecVideo(trackInfo: MediaCodecTrackInfo): Boolean {
        val decoder: MediaCodec = trackInfo.decoder
        val encoder: MediaCodec = trackInfo.encoder
        val extractor: MediaExtractor = trackInfo.extractor
        var finishAll = false
        var finishDecodeInput = false
        var finishDecodeOutput = false
        var frameNumber = -1
        while (!finishAll) {
            if (!finishDecodeInput) {
                val inputIndex = decoder.dequeueInputBuffer(INPUT_TIMEOUT)
                if (inputIndex >= 0) {
                    val inputBuffer: ByteBuffer? = decoder.getInputBuffer(inputIndex)
                    if (inputBuffer != null) {
                        inputBuffer.clear()
                        val readSampleData = extractor.readSampleData(inputBuffer, 0)
                        if (readSampleData < 0) {
                            decoder.queueInputBuffer(
                                inputIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            finishDecodeInput = true
                        } else {
                            decoder.queueInputBuffer(
                                inputIndex, 0, readSampleData, extractor.sampleTime, 0
                            )
                            extractor.advance()
                        }
                    }
                }
            }
            if (checkInterruptedThread()) {
                return false
            }
            if (!finishDecodeOutput) {
                val outputInfo = MediaCodec.BufferInfo()
                val outputIndex = decoder.dequeueOutputBuffer(outputInfo, OUTPUT_TIMEOUT)
                if (outputIndex >= 0) {
                    if (outputInfo.size != 0) {
                        videoCodecListener?.onDecoderOutput(
                            this, outputIndex, decoder, encoder, outputInfo
                        )
                        decoder.releaseOutputBuffer(outputIndex, false)
                    }
                    if (outputInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        encoder.signalEndOfInputStream()
                        finishDecodeOutput = true
                    }
                }
            }
            if (checkInterruptedThread()) {
                return false
            }
            var enableMuxer = true
            while (enableMuxer) {
                val outputInfo = MediaCodec.BufferInfo()
                val index = encoder.dequeueOutputBuffer(outputInfo, MUXER_TIMEOUT)
                if (index >= 0) {
                    val outBuffer: ByteBuffer? = encoder.getOutputBuffer(index)
                    if ((outputInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        outputInfo.size = 0
                    }
                    if (outBuffer != null && outputInfo.size != 0) {
                        if (frameNumber < 0) {
                            frameNumber = 0
                        } else {
                            frameNumber++
                        }
                        outputInfo.presentationTimeUs =
                            (1000000f / frameRate * frameNumber).toLong()
                        outBuffer.position(outputInfo.offset)
                        outBuffer.limit(outputInfo.offset + outputInfo.size)
                        videoCodecListener?.onEncoderOutput(this, index, encoder, outputInfo)
                        writeMuxerData(trackInfo.muxerTrackIndex.get(), outBuffer, outputInfo)
                        onVideoProgressUpdate(outputInfo.presentationTimeUs)
                    } else {
                        enableMuxer = false
                    }
                    encoder.releaseOutputBuffer(index, false)
                    finishAll = outputInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                    if (finishAll) {
                        break
                    }
                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    onVideoEncodeFormatChanged(encoder.outputFormat)
                } else {
                    enableMuxer = false
                }
            }
            if (!finishAll && checkInterruptedThread()) {
                return false
            }
        }
        return true
    }

}