package  com.zpf.aaa.synth

interface IMediaSynthTrackInput {
    fun hasInputConfig(): Boolean
    fun isRunning(): Boolean
    fun start()
    fun stop()
}