package com.zpf.media.synth.base

import android.media.MediaCodec
import com.zpf.media.synth.CodecEditor
import com.zpf.media.synth.ExtractorEditor
import com.zpf.media.synth.i.ISynthInputPart
import com.zpf.media.synth.i.ISynthTrackEditor
import com.zpf.media.synth.model.MediaOutputBasicInfo
import com.zpf.media.synth.model.MediaSynthStatus
import com.zpf.media.synth.model.MediaSynthTrackId
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
        val partIndex = recorder.trackPartIndex.get()
        var isUnknowType = true
        if (editor is ExtractorEditor) {
            isUnknowType = false
        }
        if (editor.trackId() == MediaSynthTrackId.VIDEO) {
            releaseSurface()
        }
        if (editor is CodecEditor) {
            isUnknowType = false
            if (editor.encoder != null) {
                mediaEncoderOutputSurface = surfaceManager?.getEncoderOutputSurface(partIndex)
                editor.encoder.configure(
                    editor.encoderFormat,
                    mediaEncoderOutputSurface,
                    null,
                    MediaCodec.CONFIGURE_FLAG_ENCODE
                )
                if (editor.isEncodeInputBySurface()) {
                    val surface = editor.encoder.createInputSurface()
                    mediaEncoderInputSurface = surface
                    surfaceManager?.onEncoderInputSurfaceCreated(partIndex, surface)
                }
            }
            if (editor.decoder != null) {
                mediaDecoderOutputSurface = surfaceManager?.getDecoderOutputSurface(partIndex)
                editor.decoder.configure(
                    editor.decoderFormat, mediaDecoderOutputSurface, null, 0
                )
                if (editor.isDecodeInputBySurface()) {
                    val surface = editor.decoder.createInputSurface()
                    mediaDecoderInputSurface = surface
                    surfaceManager?.onDecoderInputSurfaceCreated(partIndex, surface)
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

    private fun onTrackPartStart(
        editor: ISynthTrackEditor, recorder: MediaTrackRecorder, initConfig: Boolean
    ) {
        MediaSynthLogger.logInfo("onTrackPartStart==>id=${recorder.trackId};index=${recorder.trackPartIndex};progressTime=${recorder.trackProgressTime}")
        if (!prepareTrackThread(editor, recorder, initConfig)) {
            doTrackThreadJob(editor, recorder, initConfig)
        }
    }

    private fun onTrackPartFinish(recorder: MediaTrackRecorder) {
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
            onTrackPartStart(editor, recorder, true)
        } else if (isFinished()) {
            changeToStatus(MediaSynthStatus.COMPLETE)
        }
    }

    private fun prepareTrackThread(
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
        MediaSynthLogger.logInfo("doTrackThreadJob==>trackId=${editor.trackId()};index=${recorder.trackPartIndex.get()}")
        if (requireInterruptedOrBlock()) {
            return
        }
        if (initConfig) {
            try {
                onConfigureTrackPart(editor, recorder)
            } catch (e: InterruptedException) {
                MediaSynthLogger.logInfo(e.message)
            } catch (e: Throwable) {
                MediaSynthLogger.logError(e.message)
                changeToStatus(MediaSynthStatus.ERROR)
            }
        }
        if (requireInterruptedOrBlock()) {
            return
        }
        editor.start()
        try {
            handleTrackInput(editor, recorder)
        } catch (e: InterruptedException) {
            MediaSynthLogger.logInfo(e.message)
        } catch (e: Throwable) {
            MediaSynthLogger.logError(e.message)
            changeToStatus(MediaSynthStatus.ERROR)
        }
        editor.stop()
        if (requireInterruptedOrBlock()) {
            return
        }
        onTrackPartFinish(recorder)
    }
}