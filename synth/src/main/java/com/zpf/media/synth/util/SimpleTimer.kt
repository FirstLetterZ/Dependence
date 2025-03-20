package  com.zpf.media.synth.util

class SimpleTimer {
    @Volatile
    private var startTime = 0L

    @Volatile
    private var takeTime = 0L

    @Volatile
    private var isStopped = true

    fun start() {
        if (isStopped) {
            takeTime = 0L
        } else {
            updateTakeTime()
        }
        startTime = System.currentTimeMillis()
        isStopped = false
    }

    fun pause() {
        if (isStopped) {
            return
        }
        updateTakeTime()
    }

    fun stop() {
        isStopped = true
        updateTakeTime()
    }

    fun getTime(): Long {
        return if (startTime > 0L) {
            System.currentTimeMillis() - startTime + takeTime
        } else {
            takeTime
        }
    }

    private fun updateTakeTime() {
        if (startTime > 0L) {
            takeTime += System.currentTimeMillis() - startTime
        }
        startTime = 0L
    }

}