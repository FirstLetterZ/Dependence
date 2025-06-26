package com.zpf.aaa.videotool

import android.content.Context
import android.os.Handler
import com.google.android.exoplayer2.audio.AudioCapabilities
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.audio.AudioSink
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecAdapter
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.util.MediaClock

class MultiMediaCodecAudioRender : MediaCodecAudioRenderer {
    constructor(context: Context, mediaCodecSelector: MediaCodecSelector) : super(
        context, mediaCodecSelector
    )

    constructor(
        context: Context,
        mediaCodecSelector: MediaCodecSelector,
        eventHandler: Handler?,
        eventListener: AudioRendererEventListener?
    ) : super(context, mediaCodecSelector, eventHandler, eventListener)

    constructor(
        context: Context,
        mediaCodecSelector: MediaCodecSelector,
        eventHandler: Handler?,
        eventListener: AudioRendererEventListener?,
        audioCapabilities: AudioCapabilities,
        vararg audioProcessors: AudioProcessor
    ) : super(
        context,
        mediaCodecSelector,
        eventHandler,
        eventListener,
        audioCapabilities,
        *audioProcessors
    )

    constructor(
        context: Context,
        mediaCodecSelector: MediaCodecSelector,
        eventHandler: Handler?,
        eventListener: AudioRendererEventListener?,
        audioSink: AudioSink
    ) : super(context, mediaCodecSelector, eventHandler, eventListener, audioSink)

    constructor(
        context: Context,
        mediaCodecSelector: MediaCodecSelector,
        enableDecoderFallback: Boolean,
        eventHandler: Handler?,
        eventListener: AudioRendererEventListener?,
        audioSink: AudioSink
    ) : super(
        context, mediaCodecSelector, enableDecoderFallback, eventHandler, eventListener, audioSink
    )

    constructor(
        context: Context,
        codecAdapterFactory: MediaCodecAdapter.Factory,
        mediaCodecSelector: MediaCodecSelector,
        enableDecoderFallback: Boolean,
        eventHandler: Handler?,
        eventListener: AudioRendererEventListener?,
        audioSink: AudioSink
    ) : super(
        context,
        codecAdapterFactory,
        mediaCodecSelector,
        enableDecoderFallback,
        eventHandler,
        eventListener,
        audioSink
    )


    override fun getMediaClock(): MediaClock? {
        return super.getMediaClock()
    }
}