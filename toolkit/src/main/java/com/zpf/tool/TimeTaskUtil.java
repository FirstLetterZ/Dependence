package com.zpf.tool;

import com.zpf.tool.config.MainHandler;

import java.util.Timer;
import java.util.TimerTask;

public abstract class TimeTaskUtil {
    private Timer timer;
    private TimerTask timerTask;
    private long timeInterval = 3000;//间隔
    private volatile boolean isRunning = false;

    //停止轮播
    public void stopPlay() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        isRunning = false;
    }

    //开始轮播
    public void startPlay(long delay) {
        startPlay(delay, -99);
    }

    public void startPlay(long delay, long interval) {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    doInChildThread();
                    MainHandler.get().post(new Runnable() {
                        @Override
                        public void run() {
                            doInMainThread();
                        }
                    });
                }
            };
        }
        if (interval > 0) {
            timeInterval = interval;
        }
        if (isRunning) {
            return;
        }
        isRunning = true;
        try {
            timer.schedule(timerTask, delay, timeInterval);//轮播间隔
        } catch (Exception e) {
            isRunning = false;
            e.printStackTrace();
        }
    }

    protected abstract void doInMainThread();

    protected abstract void doInChildThread();

    public long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }
}
