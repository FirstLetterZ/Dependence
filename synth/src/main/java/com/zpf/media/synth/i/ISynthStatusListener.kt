package  com.zpf.media.synth.i

interface ISynthStatusListener {
    fun onProgress(presentationTimeUs: Long, durationUs: Long, completed: Boolean)
    fun onStatusChanged(oldCode: Int, newCode: Int)
}