package com.zpf.aaa.synth

import android.media.MediaCodec


abstract class BaseMediaSynth2(
    inputs: List<MediaSynthInput>, output: MediaSynthOutput
) : BaseMediaSynth(inputs, output) {

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
            if (videoInput != null) {
                onConfigure(videoInput)
                videoTrackRecorder.trackProgressTime.set(0L)
            }
            if (audioInput != null) {
                onConfigure(audioInput)
                audioTrackRecorder.trackProgressTime.set(0L)
            }
        }
        var shouldNotify = false
        if (videoInput != null) {
            if (videoInput.isRunning()) {
                shouldNotify = true
            } else {
                videoInput.start()
                runVideoInput(videoInput, videoTrackRecorder)
            }
        }
        if (audioInput != null) {
            if (audioInput.isRunning()) {
                shouldNotify = true
            } else {
                audioInput.start()
                runAudioInput(audioInput, audioTrackRecorder)
            }
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
            if (inputConfig.decoder != null) {
                inputConfig.decoder.configure(
                    inputConfig.decoderFormat, mediaDecoderOutputSurface, null, 0
                )
                if (inputConfig.isDecodeInputBySurface()) {
                    mediaDecoderInputSurface?.release()
                    mediaDecoderInputSurface = inputConfig.decoder.createInputSurface()
                    mediaDecoderSurfaceListener?.onDecoderInputSurfaceCreated(
                        mediaDecoderInputSurface
                    )
                }
            }
            if (inputConfig.encoder != null) {
                inputConfig.encoder.configure(
                    inputConfig.encoderFormat,
                    mediaEncoderOutputSurface,
                    null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE
                )
                if (inputConfig.isEncodeInputBySurface()) {
                    mediaEncoderInputSurface?.release()
                    mediaEncoderInputSurface = inputConfig.encoder.createInputSurface()
                    mediaEncoderSurfaceListener?.onEncoderInputSurfaceCreated(
                        mediaEncoderInputSurface
                    )
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
        outputWriter.release()
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