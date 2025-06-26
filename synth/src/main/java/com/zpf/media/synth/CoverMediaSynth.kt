package com.zpf.media.synth

import android.annotation.SuppressLint
import android.media.MediaCodec
import com.zpf.media.synth.i.ISynthInputPart
import com.zpf.media.synth.i.ISynthTrackEditor
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaOutputBasicInfo
import com.zpf.media.synth.model.MediaSynthStatus
import com.zpf.media.synth.model.MediaSynthTrackId
import com.zpf.media.synth.model.MediaTrackRecorder

class CoverMediaSynth(
    outputInfo: MediaOutputBasicInfo,
    inputs: List<ISynthInputPart>,
    writer: ISynthTrackWriter,
    private val coverFrameCount: Int
) : SyncMediaSynth(outputInfo, inputs, writer) {

    override fun handleTrackInput(editor: ISynthTrackEditor, recorder: MediaTrackRecorder) {
        if (recorder.trackPartIndex.get() == 0) {
            if (recorder.trackId == MediaSynthTrackId.VIDEO) {
                if (!handleCover(editor)) {
                    changeToStatus(MediaSynthStatus.WRITE_ERROR)
                }
            } else if (recorder.trackId == MediaSynthTrackId.AUDIO) {
                val format = editor.getInputFormat() ?: return
                writeEmptyVoice(format, getPartStartTime(0), getPartStartTime(1))
            }
        } else {
            super.handleTrackInput(editor, recorder)
        }
    }

    @SuppressLint("WrongConstant")
    private fun handleCover(editor: ISynthTrackEditor): Boolean {
        val encoder = (editor as? CodecEditor)?.encoder ?: return false
        var tryTime = 0
        val trackId = editor.trackId()
        val frameTime = 1000000L / getOutputBasicInfo().frameRate
        while (tryTime < 100) {
            tryTime++
            val outputInfo = MediaCodec.BufferInfo()
            val outputIndex = encoder.dequeueOutputBuffer(outputInfo, OUTPUT_TIMEOUT)
            if (outputIndex >= 0) {
                val outBuffer = encoder.getOutputBuffer(outputIndex) ?: continue
                if (outputInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    outputInfo.presentationTimeUs = 0L
                    writer.write(trackId, outBuffer, outputInfo)
                    outputInfo.size = 0
                }
                //todo zpf 优化 时间计算帧数
                if (outputInfo.size > 0) {
                    var frameIndex = 0
                    for (i in 0 until coverFrameCount) {
                        outBuffer.position(outputInfo.offset)
                        outBuffer.limit(outputInfo.offset + outputInfo.size)
                        outputInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                        outputInfo.presentationTimeUs = frameTime * frameIndex
                        writer.write(trackId, outBuffer, outputInfo)
                        updateTrackProgress(trackId, outputInfo.presentationTimeUs)
                        frameIndex++
                    }
                    encoder.signalEndOfInputStream()
                    encoder.releaseOutputBuffer(outputIndex, false)
                    return true
                } else {
                    encoder.releaseOutputBuffer(outputIndex, false)
                }
            } else if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                writer.setFormat(trackId, encoder.outputFormat)
            } else {
                Thread.sleep(20L)
            }
        }
        return false
    }

}