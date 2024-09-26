package  com.zpf.aaa.synth

interface ISynthStatusListener {
    fun onProgress(presentationTimeUs: Long, durationUs: Long, completed: Boolean)
    fun onStatusChanged(oldCode: Int, newCode: Int)
}