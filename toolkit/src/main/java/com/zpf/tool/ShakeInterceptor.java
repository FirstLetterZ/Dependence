package com.zpf.tool;

/**
 * Created by ZPF on 2018/12/4.
 */
public class ShakeInterceptor {

    private volatile boolean paused = false;
    private long lastRecord = 0;
    private long timeInterval = 200;

    public ShakeInterceptor() {
    }

    public ShakeInterceptor(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public boolean checkInterval() {
        if (!paused && (System.currentTimeMillis() - lastRecord) > timeInterval) {
            lastRecord = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public void record() {
        lastRecord = System.currentTimeMillis();
    }

    public void pause() {
        paused = true;
    }

    public void start() {
        paused = false;
    }

    public void reset() {
        lastRecord = 0;
    }
}
