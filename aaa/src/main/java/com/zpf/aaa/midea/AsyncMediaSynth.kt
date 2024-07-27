package com.zpf.aaa.midea

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.os.HandlerThread

class AsyncMediaSynth(
    videoTrack: MediaCodecTrackInfo,
    audioTrack: MediaTrackInfo?,
    mediaMuxer: MediaMuxer,
    retriever: MediaMetadataRetriever,
    mediaInfo: MediaInfo
) : AbsMediaSynth(videoTrack, audioTrack, mediaMuxer, retriever, mediaInfo) {
    private val frameRate: Int
    private var frameNumber = -1

    init {
        val videoFormat = videoTrack.encodeFormat
        val rate = videoFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
        frameRate = if (rate > 0) {
            rate
        } else {
            30
        }
    }

    private val decoderCallback = object : MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
            val buffer = codec.getInputBuffer(index)
            if (buffer != null) {
                buffer.clear()
                val readSampleData = videoTrack.extractor.readSampleData(buffer, 0)
                if (readSampleData <= 0) {
                    codec.queueInputBuffer(
                        index, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    )
                } else {
                    codec.queueInputBuffer(
                        index, 0, readSampleData, videoTrack.extractor.sampleTime, 0
                    )
                    videoTrack.extractor.advance()
                }
            }
        }

        override fun onOutputBufferAvailable(
            codec: MediaCodec, index: Int, info: MediaCodec.BufferInfo
        ) {
            videoCodecListener?.onDecoderOutput(
                this@AsyncMediaSynth, index, codec, videoTrack.encoder, info
            )
            codec.releaseOutputBuffer(index, false)
            if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                videoTrack.encoder.signalEndOfInputStream()
            }
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
        }
    }

    private val encoderCallback = object : MediaCodec.Callback() {
        override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
        }

        override fun onOutputBufferAvailable(
            codec: MediaCodec, index: Int, info: MediaCodec.BufferInfo
        ) {
            val outputBuffer = codec.getOutputBuffer(index)
            if (outputBuffer != null && info.size != 0) {
                if (frameNumber < 0) {
                    frameNumber = 0
                } else {
                    frameNumber++
                }
                info.presentationTimeUs =
                    (1000000f / frameRate * frameNumber).toLong()
                outputBuffer.position(info.offset)
                outputBuffer.limit(info.offset + info.size)
                videoCodecListener?.onEncoderOutput(this@AsyncMediaSynth, index, codec, info)
                writeMuxerData(videoTrack.muxerTrackIndex.get(), outputBuffer, info)
                onVideoProgressUpdate(info.presentationTimeUs)
            }
            codec.releaseOutputBuffer(index, true)
            if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                videoTrack.completed.set(1)
                checkFinish()
            }
        }

        override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
        }

        override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
            onVideoEncodeFormatChanged(format)
        }
    }

    override fun configureVideoTrack(trackInfo: MediaTrackInfo) {
        super.configureVideoTrack(trackInfo)
        frameNumber = -1
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
                val thread = object : HandlerThread("video_codec") {
                    override fun onLooperPrepared() {
                        super.onLooperPrepared()
                        unit.decoder.setCallback(decoderCallback)
                        unit.encoder.setCallback(encoderCallback)
                        videoThread = null
                    }
                }
                thread.start()
                videoThread = thread
            } else {
                threadLock.notifyAll()
            }
        }
    }

}