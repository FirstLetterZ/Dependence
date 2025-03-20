package  com.zpf.media.synth.model

data class MediaOutputBasicInfo(
    val mime: String,
    val width: Int,
    val height: Int,
    val duration: Long,
    val frameRate: Int,
)