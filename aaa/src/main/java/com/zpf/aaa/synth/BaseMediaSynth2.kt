package com.zpf.aaa.synth

import android.media.MediaCodec


abstract class BaseMediaSynth2(
    inputs: List<MediaSynthInput>, writer: ISynthOutputWriter, outputInfo: MediaOutputBasicInfo
) : BaseMediaSynth(inputs, writer, outputInfo) {
    protected var videoWorkThread: Thread? = null
    protected var audioWorkThread: Thread? = null

    override fun onStart(initConfig: Boolean) {
        val videoInput = getCurrentInputConfig(MediaSynthTrack.VIDEO_TRACK)
        val audioInput = getCurrentInputConfig(MediaSynthTrack.AUDIO_TRACK)
        if (videoInput == null && audioInput == null) {
            if (isFinished()) {
                changeToStatus(MediaSynthStatus.CONFIG_ERROR)
            } else {
                changeToStatus(MediaSynthStatus.INDEX_NULL_ERROR)
            }
            return
        }
        if (initConfig) {
            resetProgress()
        }
        var shouldNotify = false
        if (videoWorkThread?.isAlive != true) {
            val thread = Thread {
                if (videoInput != null) {
                    if (initConfig) {
                        onConfigure(videoInput)
                        videoTrackRecorder.trackProgressTime.set(0L)
                    }
                    videoInput.start()
                    runVideoInput(videoInput, videoTrackRecorder)
                }
            }
            thread.start()
            videoWorkThread = thread
        } else {
            shouldNotify = true
        }
        if (audioWorkThread?.isAlive != true) {
            val thread = Thread {
                if (audioInput != null) {
                    if (initConfig) {
                        onConfigure(audioInput)
                        audioTrackRecorder.trackProgressTime.set(0L)
                    }
                    audioInput.start()
                    runAudioInput(audioInput, audioTrackRecorder)
                }
            }
            thread.start()
            audioWorkThread = thread
        } else {
            shouldNotify = true
        }
        if (shouldNotify) {
            notifyWorkThread()
        }
    }

    protected open fun onConfigure(inputConfig: IMediaSynthTrackInput) {
        var isUnknowType = true
        if (inputConfig is MediaExtractorInput) {
            isUnknowType = false
            inputConfig.extractor?.selectTrack(inputConfig.trackIndex)
        }
        if (inputConfig is MediaCodecInput) {
            isUnknowType = false
            if (inputConfig.encoder != null) {
                inputConfig.encoder.configure(
                    inputConfig.encoderFormat,
                    mediaEncoderOutputSurface,
                    null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE
                )
                if (inputConfig.isEncodeInputBySurface()) {
                    mediaEncoderInputSurface?.release()
                    val surface = inputConfig.encoder.createInputSurface()
                    mediaEncoderInputSurface = surface
                    mediaEncoderSurfaceListener?.onSurfaceCreated(surface)

                }
            }
            if (inputConfig.decoder != null) {
                inputConfig.decoder.configure(
                    inputConfig.decoderFormat, mediaDecoderOutputSurface, null, 0
                )
                if (inputConfig.isDecodeInputBySurface()) {
                    mediaDecoderInputSurface?.release()
                    val surface = inputConfig.decoder.createInputSurface()
                    mediaDecoderInputSurface = surface
                    mediaDecoderSurfaceListener?.onSurfaceCreated(surface)
                }
            }
        }
        if (isUnknowType) {
            onConfigureUnknowTypeConfig(inputConfig)
        }
    }

    override fun onStop() {
        val videoInput = getCurrentInputConfig(MediaSynthTrack.VIDEO_TRACK)
        val audioInput = getCurrentInputConfig(MediaSynthTrack.AUDIO_TRACK)
        if (videoInput == null && audioInput == null) {
            onReleased()
            return
        }
        if (videoInput?.isRunning() == true || audioInput?.isRunning() == true) {
            notifyWorkThread()
        } else {
            onReleased()
        }
    }

    protected open fun onReleased() {
        inputs.getOrNull(videoTrackRecorder.trackInputIndex.get())?.let {
            it.videoTrackInput?.stop()
        }
        inputs.getOrNull(audioTrackRecorder.trackInputIndex.get())?.let {
            it.audioTrackInput?.stop()
        }
        outputWriter.stop()
        mediaDecoderInputSurface?.release()
        mediaEncoderInputSurface?.release()
        mediaDecoderInputSurface = null
        mediaEncoderInputSurface = null
    }

    protected abstract fun onConfigureUnknowTypeConfig(inputConfig: IMediaSynthTrackInput)
    protected abstract fun runVideoInput(
        inputConfig: IMediaSynthTrackInput,
        inputRecorder: MediaTrackRecorder
    )

    protected abstract fun runAudioInput(
        inputConfig: IMediaSynthTrackInput,
        inputRecorder: MediaTrackRecorder
    )

}