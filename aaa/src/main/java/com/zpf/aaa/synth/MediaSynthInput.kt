package  com.zpf.aaa.synth

import androidx.annotation.IntRange

class MediaSynthInput(
    val mediaInfo: MediaInputBasicInfo,
    val videoTrackInput: IMediaSynthTrackInput?,
    val audioTrackInput: IMediaSynthTrackInput?
) {

    fun getTrackInput(
        @IntRange(
            from = MediaSynthTrack.VIDEO_TRACK.toLong(), to = MediaSynthTrack.AUDIO_TRACK.toLong()
        ) id: Int
    ): IMediaSynthTrackInput? {
        return when (id) {
            MediaSynthTrack.VIDEO_TRACK -> videoTrackInput
            MediaSynthTrack.AUDIO_TRACK -> audioTrackInput
            else -> null
        }
    }
}


