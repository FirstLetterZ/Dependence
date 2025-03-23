package com.zpf.media.synth.base

import com.zpf.media.synth.i.IMediaSynth
import com.zpf.media.synth.i.ISynthStatusListener
import com.zpf.media.synth.model.MediaSynthStatus
import com.zpf.media.synth.util.MediaSynthLogger
import com.zpf.media.synth.util.SimpleTimer
import java.util.concurrent.atomic.AtomicInteger

abstract class MediaSynthStateManager : IMediaSynth {
    protected val statusCode = AtomicInteger(MediaSynthStatus.CREATE)
    protected val statusListenerSet = HashSet<ISynthStatusListener>()
    protected val workThreadLock = Object()
    private val timer = SimpleTimer()

    override fun status(): Int {
        return statusCode.get()
    }

    override fun start() {
        changeToStatus(MediaSynthStatus.START)
    }

    override fun pause() {
        changeToStatus(MediaSynthStatus.PAUSE)
    }

    override fun stop() {
        changeToStatus(MediaSynthStatus.STOP)
    }

//    override fun reset() {
//        changeToStatus(MediaSynthStatus.CREATE)
//    }

    override fun addStatusListener(listener: ISynthStatusListener) {
        statusListenerSet.add(listener)
    }

    override fun removeStatusListener(listener: ISynthStatusListener) {
        statusListenerSet.remove(listener)
    }

    override fun takeTime(): Long {
        return timer.getTime()
    }

    protected open fun onStateChanged(oldCode: Int, newCode: Int) {
        when (newCode) {
            MediaSynthStatus.COMPLETE -> {
                timer.stop()
                onClear()
            }
            MediaSynthStatus.STOP -> {
                onStop()
                timer.stop()
            }
            MediaSynthStatus.PAUSE -> {
                timer.pause()
            }
            MediaSynthStatus.START -> {
                onStart(oldCode == MediaSynthStatus.CREATE)
                timer.start()
            }
            MediaSynthStatus.CREATE -> {

            }
            else -> {
                onStop()
                timer.stop()
            }
        }
        MediaSynthLogger.logInfo("onStateChanged==>oldCode=$oldCode;newCode=$newCode")
    }

    protected fun changeToStatus(newCode: Int): Boolean {
        val oldCode = status()
        MediaSynthLogger.logInfo("changeToStatus==>oldCode=$oldCode;newCode=$newCode")
        if (enableChangeStatus(oldCode, newCode)) {
            statusCode.set(newCode)
            onStateChanged(oldCode, newCode)
            statusListenerSet.forEach {
                it.onStatusChanged(oldCode, newCode)
            }
            return true
        }
        return false
    }

    protected open fun enableChangeStatus(oldCode: Int, newCode: Int): Boolean {
        val enable = when (newCode) {
            MediaSynthStatus.CREATE -> {
                oldCode == MediaSynthStatus.STOP || oldCode == MediaSynthStatus.COMPLETE
            }
            MediaSynthStatus.START -> {
                oldCode == MediaSynthStatus.CREATE || oldCode == MediaSynthStatus.PAUSE
            }
            MediaSynthStatus.PAUSE -> {
                oldCode == MediaSynthStatus.START
            }
            MediaSynthStatus.STOP -> {
                oldCode == MediaSynthStatus.START || oldCode == MediaSynthStatus.PAUSE
            }
            MediaSynthStatus.COMPLETE -> {
                oldCode == MediaSynthStatus.START || oldCode == MediaSynthStatus.PAUSE
            }
            else -> {
                true
            }
        }
        return enable
    }

    protected fun notifyWorkThread() {
        synchronized(workThreadLock) {
            workThreadLock.notifyAll()
        }
    }

    protected fun requireInterruptedOrBlock(): Boolean {
        var code = status()
        return when (code) {
            MediaSynthStatus.STOP -> {
                true
            }
            MediaSynthStatus.PAUSE -> {
                synchronized(workThreadLock) {
                    code = status()
                    return when (code) {
                        MediaSynthStatus.STOP -> {
                            true
                        }
                        MediaSynthStatus.PAUSE -> {
                            try {
                                workThreadLock.wait()
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            status() == MediaSynthStatus.STOP
                        }
                        else -> false
                    }
                }
            }
            else -> false
        }
    }

    protected abstract fun onStart(initConfig: Boolean)
    protected abstract fun onStop()
    protected abstract fun onClear()
}