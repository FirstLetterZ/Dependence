package  com.zpf.media.synth

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat

class CodecEditor(
    trackId: String,
    trackIndex: Int,
    extractor: MediaExtractor?,
    val decoder: MediaCodec?,
    val decoderFormat: MediaFormat?,
    val encoder: MediaCodec?,
    val encoderFormat: MediaFormat?
) : ExtractorEditor(trackId, trackIndex, null, extractor) {
    override fun isValid(): Boolean {
        return super.isValid() || decoder != null || encoder != null
    }

    override fun getInputFormat(): MediaFormat? {
        return decoderFormat ?: super.getInputFormat()
    }

    override fun start() {
        if (isStarted.get()) {
            return
        }
        super.start()
        decoder?.start()
        encoder?.start()
    }

    override fun stop() {
        if (!isStarted.get()) {
            return
        }
        super.stop()
        decoder?.stop()
        decoder?.release()
        encoder?.stop()
        encoder?.release()
    }

    fun isDecodeInputBySurface(): Boolean {
        if (decoder == null || decoderFormat == null) {
            return false
        }
        return try {
            decoderFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT) == MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        } catch (e: Exception) {
            false
        }
    }

    fun isEncodeInputBySurface(): Boolean {
        if (encoder == null || encoderFormat == null) {
            return false
        }
        return try {
            encoderFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT) == MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        } catch (e: Exception) {
            false
        }
    }
}