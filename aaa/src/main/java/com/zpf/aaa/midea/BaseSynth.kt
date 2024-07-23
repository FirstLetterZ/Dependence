package com.zpf.aaa.midea

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.util.Log
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseSynth(
    protected val videoUnit: MediaUnit?,
    protected val audioUnit: MediaUnit?,
    protected val mediaMuxer: MediaMuxer,
    val retriever: MediaMetadataRetriever,
    val mediaInfo: MediaInfo
) : IMediaSynth {
    protected val INPUT_TIMEOUT = 0L
    protected val OUTPUT_TIMEOUT = 0L
    protected val MUXER_TIMEOUT = 0L
    protected val statusCode = AtomicInteger(0)
    protected var videoThread: Thread? = null
    protected var audioThread: Thread? = null
    protected var videoMuxerTrack: Int = -1
    protected var audioMuxerTrack: Int = -1
    protected val muxerStarted = AtomicBoolean(false)
    var progressListener: IMediaSynthListener? = null
    var outputListener: IMediaEncodeListener? = null
    protected val muxerLock = Object()

    override fun status(): Int = statusCode.get()


    override fun start(executor: Executor) {
        val code = status()
        if (code == MediaSynthStatus.CREATE) {
            statusCode.set(MediaSynthStatus.START)
            videoMuxerTrack = -1
            audioMuxerTrack = -1
            videoUnit?.let {
                it.extractor.selectTrack(it.trackIndex)
                it.decoder.configure(it.decodeFormat, null, null, 0)
                it.encoder.configure(it.encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                onConfigured(it)
            }
            audioUnit?.let {
                it.extractor.selectTrack(it.trackIndex)
                it.decoder.configure(it.decodeFormat, null, null, 0)
                it.encoder.configure(it.encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                onConfigured(it)
            }
            videoUnit?.let {
                synchronized(this.javaClass) {
                    if (videoThread?.isAlive != true) {
                        val thread = Thread {
                            onCodecVideo(it.decoder, it.encoder, it.extractor)
                        }
                        thread.start()
                        videoThread = thread
                    } else {
                        //todo zpf 唤醒
                    }
                }
            }
            audioUnit?.let {
                synchronized(this.javaClass) {
                    if (audioThread?.isAlive != true) {
                        val thread = Thread {
                            onCodecAudio(it.decoder, it.encoder, it.extractor)
                        }
                        thread.start()
                        audioThread = thread
                    } else {
                        //todo zpf 唤醒
                    }
                }
            }
        } else if (code == MediaSynthStatus.PAUSE) {
            statusCode.set(MediaSynthStatus.START)
            //todo 检查线程状态
        } else {

        }
    }

    override fun pause() {
        val code = status()
        if (code == MediaSynthStatus.START) {
            statusCode.set(MediaSynthStatus.PAUSE)
        }
    }

    override fun stop() {
        val code = status()
        if (code == MediaSynthStatus.START || code == MediaSynthStatus.PAUSE) {
            statusCode.set(MediaSynthStatus.COMPLETE)
            if (videoThread?.isAlive != true) {

            }
            videoUnit?.let {
                if (code == MediaSynthStatus.START) {
                    it.decoder.stop()
                    it.encoder.stop()
                }
            }
            audioUnit?.let {
                if (code == MediaSynthStatus.START) {
                    it.decoder.stop()
                    it.encoder.stop()
                }
            }
        }
    }

    override fun release() {
        val code = status()
        if (code != MediaSynthStatus.RELEASE) {
            statusCode.set(MediaSynthStatus.RELEASE)
            if (code != MediaSynthStatus.START) {
                onReleased(code)
            }
        }
    }

    protected fun onVideoEncodeFormatChanged(mediaFormat: MediaFormat): Int {
        val index = mediaMuxer.addTrack(mediaFormat)
        videoMuxerTrack = index
        checkStartMuxer()
        return index
    }

    protected fun onAudioEncodeFormatChanged(mediaFormat: MediaFormat): Int {
        val index = mediaMuxer.addTrack(mediaFormat)
        audioMuxerTrack = index
        checkStartMuxer()
        return index
    }

    protected fun checkStartMuxer() {
        synchronized(muxerLock) {
            if (muxerStarted.get()) {
                Log.e("ZPF", "checkStartMuxer==>error")
                //todo zpf
            } else if ((audioUnit == null || audioMuxerTrack >= 0) && (videoUnit == null || videoMuxerTrack >= 0)) {
                mediaMuxer.start()
                muxerStarted.set(true)
            } else {

            }
        }
    }

    protected fun writeMuxerData(
        index: Int, buffer: ByteBuffer, outputInfo: MediaCodec.BufferInfo
    ) {
        if (!muxerStarted.get()) {
            synchronized(muxerLock) {
                if (!muxerStarted.get()) {
                    try {
                        muxerLock.wait()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        if (status() != MediaSynthStatus.RELEASE) {
            mediaMuxer.writeSampleData(index, buffer, outputInfo)
        }
    }

    protected abstract fun onCodecVideo(
        decoder: MediaCodec, encoder: MediaCodec, extractor: MediaExtractor
    )

    protected abstract fun onCodecAudio(
        decoder: MediaCodec, encoder: MediaCodec, extractor: MediaExtractor
    )

    protected open fun onConfigured(unit: MediaUnit) {
        unit.decoder.start()
        unit.encoder.start()
    }

    protected fun onReleased(lastStatus: Int) {
        videoUnit?.let {
            if (lastStatus == MediaSynthStatus.START) {
                it.decoder.stop()
                it.encoder.stop()
            }
            it.extractor.release()
            it.decoder.release()
            it.encoder.release()
        }
        audioUnit?.let {
            if (lastStatus == MediaSynthStatus.START) {
                it.decoder.stop()
                it.encoder.stop()
            }
            it.extractor.release()
            it.decoder.release()
            it.encoder.release()
        }
        if (muxerStarted.get()) {
            mediaMuxer.stop()
            mediaMuxer.release()
            muxerStarted.set(false)
        }
    }

}