package  com.zpf.media.synth.model

object MediaSynthTrackId {
    const val VIDEO = "video"
    const val AUDIO = "audio"

    fun all() = arrayListOf(VIDEO, AUDIO)
}