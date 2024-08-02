package com.zpf.aaa.synth

import android.media.MediaCodec
import android.media.MediaExtractor
import java.nio.ByteBuffer

class SyncMediaSynth(
    inputs: List<MediaSynthInput>, write: ISynthOutputWriter, outputInfo: MediaOutputBasicInfo
) : BaseMediaSynth2(inputs, write, outputInfo) {

    private val INPUT_TIMEOUT = 10L
    private val OUTPUT_TIMEOUT = 10L
    private val WRITE_TIMEOUT = 10L

    override fun onConfigureUnknowTypeConfig(inputConfig: IMediaSynthTrackInput) {
        changeToStatus(MediaSynthStatus.CONFIG_ERROR)
    }

    override fun runVideoInput(
        inputConfig: IMediaSynthTrackInput, inputRecorder: MediaTrackRecorder
    ) {
        handleInput(inputConfig, inputRecorder, MediaSynthTrack.VIDEO_TRACK)
    }

    override fun runAudioInput(
        inputConfig: IMediaSynthTrackInput, inputRecorder: MediaTrackRecorder
    ) {
        handleInput(inputConfig, inputRecorder, MediaSynthTrack.AUDIO_TRACK)
    }

    protected fun onInputFinish(
        inputConfig: IMediaSynthTrackInput, recorder: MediaTrackRecorder, trackId: Int
    ) {
        inputConfig.stop()
        if (requireInterruptedOrBlock()) {
            return
        }
        val nextConfig = getNextInputConfig(trackId)
        if (nextConfig == null) {
            if (isFinished()) {
                changeToStatus(MediaSynthStatus.COMPLETE)
            } else {
                recorder.trackProgressTime.set(getDuration())
            }
        } else {
            onConfigure(nextConfig)
            nextConfig.start()
            runVideoInput(nextConfig, recorder)
        }
    }

    protected fun handleInput(
        inputConfig: IMediaSynthTrackInput, recorder: MediaTrackRecorder, trackId: Int
    ) {
        val inputIndex = recorder.trackInputIndex.get()
        val mediaInfo = getInputInfo(inputIndex)?.mediaInfo
        if (mediaInfo == null) {
            changeToStatus(MediaSynthStatus.INDEX_NULL_ERROR)
            return
        }
        val extractor: MediaExtractor? = (inputConfig as? MediaExtractorInput)?.extractor
        val decoder: MediaCodec?
        val encoder: MediaCodec?
        val decoderInputBySurface: Boolean
        val decoderOutputBySurface: Boolean = mediaDecoderOutputSurface != null
        val encoderInputBySurface: Boolean
        val encoderOutputBySurface: Boolean = mediaEncoderOutputSurface != null
        if (inputConfig is MediaCodecInput) {
            decoder = inputConfig.decoder
            encoder = inputConfig.encoder
            decoderInputBySurface = inputConfig.isDecodeInputBySurface()
            encoderInputBySurface = inputConfig.isEncodeInputBySurface()
        } else {
            decoder = null
            encoder = null
            decoderInputBySurface = false
            encoderInputBySurface = false
        }
        val offsetTimeUs = (inputStepTimeOffsetList.getOrNull(inputIndex) ?: 0L) * 1000L
        if (encoder == null && decoder == null) {
            if (extractor == null) {
                onInputFinish(inputConfig, recorder, trackId)
                return
            } else {
                if (!outputWriter.isFormatted(trackId)) {
                    outputWriter.setFormat(
                        trackId, extractor.getTrackFormat(inputConfig.trackIndex)
                    )
                }
                copyMedia(extractor, trackId, offsetTimeUs) {
                    recorder.trackProgressTime.set(it)
                    onProgressUpdate()
                }
                onInputFinish(inputConfig, recorder, trackId)
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
        while (!finishWriteData) {
            if (requireInterruptedOrBlock()) {
                return
            }
            if (decoder != null && !finishDecodeInput) {
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
                                outputWriter.write(trackId, cacheBuffer, outputInfo)
                            }
                            outputInfo.size = 0
                        }
                        if (outputInfo.size != 0) {
                            if (frameNumber < 0) {
                                frameNumber = 1
                            } else {
                                frameNumber++
                            }
                            outputInfo.presentationTimeUs =
                                offsetTimeUs + (1000000f / frameRate * frameNumber).toLong()
                            cacheBuffer.position(outputInfo.offset)
                            cacheBuffer.limit(outputInfo.offset + outputInfo.size)
                            dispatchEncoderOutput(
                                trackId, bufferIndex, encoder, outputInfo
                            )
                            outputWriter.write(trackId, cacheBuffer, outputInfo)
                            recorder.trackProgressTime.set(outputInfo.presentationTimeUs)
                            onProgressUpdate()
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
                    if (!outputWriter.isFormatted(trackId)) {
                        outputWriter.setFormat(trackId, encoder.outputFormat)
                    }
                } else {
                    enableWrite = bufferIndex != MediaCodec.INFO_TRY_AGAIN_LATER
                }
            }
            if (decoder != null && !finishEncodeInput) {
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
                            trackId, bufferIndex, decoder, outputInfo, encoder
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
                                        outputInfo.presentationTimeUs + offsetTimeUs,
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
            }
            if (requireInterruptedOrBlock()) {
                return
            }
        }
        if (requireInterruptedOrBlock()) {
            return
        }
        onInputFinish(inputConfig, recorder, trackId)
    }

    override fun onReleased() {
        super.onReleased()
        videoWorkThread = null
        audioWorkThread = null

    }
}