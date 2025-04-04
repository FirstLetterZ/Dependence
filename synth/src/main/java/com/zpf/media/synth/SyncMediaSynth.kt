package  com.zpf.media.synth

import android.media.MediaCodec
import android.media.MediaExtractor
import com.zpf.media.synth.base.MediaSynthWriter
import com.zpf.media.synth.i.ISynthInputPart
import com.zpf.media.synth.i.ISynthTrackEditor
import com.zpf.media.synth.i.ISynthTrackWriter
import com.zpf.media.synth.model.MediaOutputBasicInfo
import com.zpf.media.synth.model.MediaSynthStatus
import com.zpf.media.synth.model.MediaTrackRecorder
import java.nio.ByteBuffer

open class SyncMediaSynth(
    outputInfo: MediaOutputBasicInfo, inputs: List<ISynthInputPart>, writer: ISynthTrackWriter
) : MediaSynthWriter(outputInfo, inputs, writer) {

    protected var INPUT_TIMEOUT = 10L
    protected var OUTPUT_TIMEOUT = 10L
    protected var WRITE_TIMEOUT = 10L

    override fun handleTrackInput(editor: ISynthTrackEditor, recorder: MediaTrackRecorder) {
        val inputIndex = recorder.trackPartIndex.get()
        val mediaInfo = inputs.getOrNull(inputIndex)?.getBasicInfo()
        if (mediaInfo == null) {
            changeToStatus(MediaSynthStatus.INDEX_NULL_ERROR)
            return
        }
        val trackId = recorder.trackId
        val extractor: MediaExtractor? = (editor as? ExtractorEditor)?.extractor
        val decoder: MediaCodec?
        val encoder: MediaCodec?
        val decoderInputBySurface: Boolean
        val decoderOutputBySurface: Boolean = mediaDecoderOutputSurface != null
        val encoderInputBySurface: Boolean
        val encoderOutputBySurface: Boolean = mediaEncoderOutputSurface != null
        if (editor is CodecEditor) {
            decoder = editor.decoder
            encoder = editor.encoder
            decoderInputBySurface = editor.isDecodeInputBySurface()
            encoderInputBySurface = editor.isEncodeInputBySurface()
        } else {
            decoder = null
            encoder = null
            decoderInputBySurface = false
            encoderInputBySurface = false
        }
        val startTimeUs = getPartStartTime(inputIndex) * 1000L
        val endTimeUs = getPartStartTime(inputIndex + 1) * 1000L
        if (decoder == null) {
            if (extractor == null) {
                return
            } else {
                if (!writer.isFormatted(trackId)) {
                    writer.setFormat(trackId, extractor.getTrackFormat(editor.trackIndex))
                }
                copyMedia(extractor, trackId, startTimeUs) { updateTrackProgress(trackId, it) }
            }
            return
        }
        var frameNumber = -1
        var finishWriteData = false
        var finishDecodeInput = false
        var finishEncodeInput = false
        var bufferIndex: Int = -1
        var cacheBuffer: ByteBuffer?
        var renderOutput: Boolean
        val frameRate = getOutputBasicInfo().frameRate
        var useInputTime = false
        val partIndex = recorder.trackPartIndex.get()
        while (!finishWriteData) {
            if (requireInterruptedOrBlock()) {
                return
            }
            if (!finishDecodeInput) {
                if (decoderInputBySurface) {
                    if (mediaDecoderInputSurface?.isValid != true) {
                        changeToStatus(MediaSynthStatus.DECODER_ERROR)
                        return
                    } else {
                        finishDecodeInput = true
                    }
                } else {
                    if (extractor == null) {
                        changeToStatus(MediaSynthStatus.DECODER_ERROR)
                        return
                    } else {
                        bufferIndex = decoder.dequeueInputBuffer(INPUT_TIMEOUT)
                        if (bufferIndex >= 0) {
                            cacheBuffer = decoder.getInputBuffer(bufferIndex)
                            if (cacheBuffer != null) {
                                cacheBuffer.clear()
                                val readSampleData = extractor.readSampleData(cacheBuffer, 0)
                                if (readSampleData < 0) {
                                    decoder.queueInputBuffer(
                                        bufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                    )
                                    finishDecodeInput = true
                                } else {
                                    decoder.queueInputBuffer(
                                        bufferIndex, 0, readSampleData, extractor.sampleTime, 0
                                    )
                                    extractor.advance()
                                }
                            }
                        }
                    }
                }
            }
            if (requireInterruptedOrBlock()) {
                return
            }
            var enableWrite = true
            while (enableWrite && encoder != null) {
                val outputInfo = MediaCodec.BufferInfo()
                bufferIndex = encoder.dequeueOutputBuffer(outputInfo, WRITE_TIMEOUT)
                if (bufferIndex >= 0) {
                    cacheBuffer = encoder.getOutputBuffer(bufferIndex)
                    renderOutput = encoderOutputBySurface && outputInfo.size != 0
                    if (cacheBuffer != null) {
                        if ((outputInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            if (encoderInputBySurface) {
                                outputInfo.presentationTimeUs += startTimeUs
                                writer.write(trackId, cacheBuffer, outputInfo)
                            }
                            outputInfo.size = 0
                        }
                        if (outputInfo.size != 0) {
                            if (frameNumber < 0) {
                                frameNumber = 1
                                useInputTime = outputInfo.presentationTimeUs <= endTimeUs
                            } else {
                                frameNumber++
                            }
                            if (useInputTime) {
                                outputInfo.presentationTimeUs += startTimeUs
                            } else {
                                outputInfo.presentationTimeUs =
                                    startTimeUs + (1000000f / frameRate * frameNumber).toLong()
                            }
                            cacheBuffer.position(outputInfo.offset)
                            cacheBuffer.limit(outputInfo.offset + outputInfo.size)
                            dispatchEncoderOutput(
                                partIndex, trackId, bufferIndex, encoder, outputInfo
                            )
                            writer.write(trackId, cacheBuffer, outputInfo)
                            updateTrackProgress(trackId, outputInfo.presentationTimeUs)
                        }
                    } else {
                        enableWrite = false
                    }
                    encoder.releaseOutputBuffer(bufferIndex, renderOutput)
                    finishWriteData = outputInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                    if (finishWriteData) {
                        break
                    }
                } else if (bufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    if (!writer.isFormatted(trackId)) {
                        writer.setFormat(trackId, encoder.outputFormat)
                    }
                } else {
                    enableWrite = bufferIndex != MediaCodec.INFO_TRY_AGAIN_LATER
                }
            }
            if (!finishEncodeInput) {
                val outputInfo = MediaCodec.BufferInfo()
                bufferIndex = decoder.dequeueOutputBuffer(outputInfo, OUTPUT_TIMEOUT)
                if (bufferIndex >= 0) {
                    if (outputInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        finishEncodeInput = true
                    }
                    if (outputInfo.size != 0) {
                        if (decoderOutputBySurface) {
                            decoder.releaseOutputBuffer(bufferIndex, true)
                        }
                        dispatchDecoderOutput(
                            partIndex, trackId, bufferIndex, decoder, outputInfo, encoder
                        )
                        if (encoder != null && !encoderInputBySurface) {
                            var failTime = 0
                            while (failTime < 10 && outputInfo.size != 0) {
                                if (failTime > 0) {
                                    Thread.sleep(10L)
                                }
                                val decoderBuffer = decoder.getOutputBuffer(bufferIndex)
                                if (decoderBuffer == null) {
                                    failTime++
                                    continue
                                }
                                val encoderBufferIndex = encoder.dequeueInputBuffer(INPUT_TIMEOUT)
                                if (encoderBufferIndex < 0) {
                                    failTime++
                                    continue
                                }
                                if (finishEncodeInput) {
                                    val encoderBuffer = encoder.getInputBuffer(encoderBufferIndex)
                                    if (encoderBuffer == null) {
                                        failTime++
                                        continue
                                    }
                                    encoderBuffer.put(decoderBuffer)
                                    encoder.queueInputBuffer(
                                        encoderBufferIndex,
                                        outputInfo.offset,
                                        outputInfo.size,
                                        outputInfo.presentationTimeUs + startTimeUs,
                                        outputInfo.flags
                                    )
                                } else {
                                    encoder.queueInputBuffer(
                                        inputIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                                    )
                                }
                            }
                        }
                        if (!decoderOutputBySurface) {
                            decoder.releaseOutputBuffer(bufferIndex, false)
                        }
                    } else {
                        decoder.releaseOutputBuffer(bufferIndex, false)
                    }
                    if (finishEncodeInput && encoder != null && encoderInputBySurface) {
                        encoder.signalEndOfInputStream()
                    }
                }
            } else if (!finishWriteData) {
                finishWriteData = encoder == null
            }
            if (requireInterruptedOrBlock()) {
                return
            }
        }
        if (requireInterruptedOrBlock()) {
            return
        }
    }

}