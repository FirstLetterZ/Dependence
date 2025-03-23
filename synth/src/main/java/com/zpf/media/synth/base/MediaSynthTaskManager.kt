package com.zpf.media.synth.base

import android.media.MediaCodec
import com.zpf.media.synth.CodecEditor
import com.zpf.media.synth.ExtractorEditor
import com.zpf.media.synth.i.ISynthInputPart
import com.zpf.media.synth.i.ISynthTrackEditor
import com.zpf.media.synth.model.MediaOutputBasicInfo
import com.zpf.media.synth.model.MediaSynthStatus
import com.zpf.media.synth.model.MediaTrackRecorder
import com.zpf.media.synth.util.MediaSynthLogger

abstract class MediaSynthTaskManager(
    outputInfo: MediaOutputBasicInfo, inputs: List<ISynthInputPart>
) : MediaSynthDispatcher(outputInfo, inputs) {
    protected abstract fun handleTrackInput(editor: ISynthTrackEditor, recorder: MediaTrackRecorder)

    override fun onStart(initConfig: Boolean) {
        var count = 0
        var shouldNotify = false
        trackRecordMap.forEach { entry ->
            val trackId = entry.key
            val recorder = entry.value
            val partInfo = getCurrentInputConfig(trackId)
            val trackEditor = partInfo?.getTrackEditor(trackId)
            if (partInfo != null) {
                if (trackEditor == null) {
                    changeToStatus(MediaSynthStatus.CONFIG_ERROR)
                    return
                }
                count++
                shouldNotify = shouldNotify || recorder.thread?.isAlive == true
                onTrackPartStart(trackEditor, recorder, initConfig)
            } else {
                onTrackPartFinish(recorder)
            }
        }
        if (count == 0) {
            if (isFinished()) {
                changeToStatus(MediaSynthStatus.CONFIG_ERROR)
            } else {
                changeToStatus(MediaSynthStatus.INDEX_NULL_ERROR)
            }
            return
        }
        if (shouldNotify) {
            notifyWorkThread()
        }
    }

    //todo zpf
    override fun onStop() {
        notifyWorkThread()
        var unfinishedCount = 0
        trackRecordMap.forEach { entry ->
            val trackId = entry.key
            val partInfo = getCurrentInputConfig(trackId)
            val trackEditor = partInfo?.getTrackEditor(trackId)
            if (partInfo != null && trackEditor != null) {
                unfinishedCount++
            }
        }
        if (unfinishedCount > 0) {
            onClear()
        }
    }

    protected open fun onConfigureTrackPart(
        editor: ISynthTrackEditor, recorder: MediaTrackRecorder
    ) {
        var isUnknowType = true
        if (editor is ExtractorEditor) {
            isUnknowType = false
        }
        if (editor is CodecEditor) {
            isUnknowType = false
            if (editor.encoder != null) {
                editor.encoder.configure(
                    editor.encoderFormat,
                    mediaEncoderOutputSurface,
                    null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE
                )
                if (editor.isEncodeInputBySurface()) {
                    mediaEncoderInputSurface?.release()
                    val surface = editor.encoder.createInputSurface()
                    mediaEncoderInputSurface = surface
                    mediaEncoderInputSurfaceListener?.onSurfaceCreated(surface)
                }
            }
            if (editor.decoder != null) {
                editor.decoder.configure(
                    editor.decoderFormat, mediaDecoderOutputSurface, null, 0
                )
                if (editor.isDecodeInputBySurface()) {
                    mediaDecoderInputSurface?.release()
                    val surface = editor.decoder.createInputSurface()
                    mediaDecoderInputSurface = surface
                    mediaDecoderInputSurfaceListener?.onSurfaceCreated(surface)
                }
            }
        }
        if (isUnknowType) {
            onConfigureUnknowEditor(editor)
        }
    }

    protected open fun onConfigureUnknowEditor(inputConfig: ISynthTrackEditor) {
        changeToStatus(MediaSynthStatus.CONFIG_ERROR)
    }

    protected fun onTrackPartStart(
        editor: ISynthTrackEditor, recorder: MediaTrackRecorder, initConfig: Boolean
    ) {
        MediaSynthLogger.logInfo("onTrackPartStart==>id=${recorder.trackId};index=${recorder.trackPartIndex};progressTime=${recorder.trackProgressTime}")
        prepareTrackThread(editor, recorder, initConfig)
    }

    protected fun onTrackPartFinish(recorder: MediaTrackRecorder) {
        MediaSynthLogger.logInfo("onTrackPartFinish==>id=${recorder.trackId};index=${recorder.trackPartIndex};progressTime=${recorder.trackProgressTime}")
        val index = if (recorder.trackPartIndex.get() < inputs.size) {
            recorder.trackPartIndex.incrementAndGet()
        } else {
            inputs.size
        }
        if (index < inputs.size) {
            val nextConfig = getCurrentInputConfig(recorder.trackId)
            if (nextConfig == null) {
                changeToStatus(MediaSynthStatus.CONFIG_ERROR)
                return
            }
            val editor = nextConfig.getTrackEditor(recorder.trackId)
            if (editor == null) {
                changeToStatus(MediaSynthStatus.CONFIG_ERROR)
                return
            }
            if (!prepareTrackThread(editor, recorder, true)) {
                doTrackThreadJob(editor, recorder, true)
            }
        } else if (isFinished()) {
            changeToStatus(MediaSynthStatus.COMPLETE)
        }
    }

    protected fun prepareTrackThread(
        editor: ISynthTrackEditor, recorder: MediaTrackRecorder, initConfig: Boolean
    ): Boolean {
        return if (recorder.thread?.isAlive != true) {
            val thread = Thread {
                doTrackThreadJob(editor, recorder, initConfig)
            }
            thread.start()
            recorder.thread = thread
            true
        } else {
            false
        }
    }

    private fun doTrackThreadJob(
        editor: ISynthTrackEditor, recorder: MediaTrackRecorder, initConfig: Boolean
    ) {
        if (requireInterruptedOrBlock()) {
            return
        }
        if (initConfig) {
            onConfigureTrackPart(editor, recorder)
        }
        editor.start()
        try {
            handleTrackInput(editor, recorder)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: Throwable) {
            MediaSynthLogger.logError(e.message)
            changeToStatus(MediaSynthStatus.ERROR)
        }
        if (requireInterruptedOrBlock()) {
            return
        }
        editor.stop()
        onTrackPartFinish(recorder)
    }
}