package com.zpf.aaa.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.zpf.tool.task.SimpleDispatcher;
import com.zpf.tool.task.SimpleTaskParam;
import com.zpf.tool.task.TaskManager;

public class DialogQueueManager extends TaskManager<SimpleTaskParam> {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public DialogQueueManager() {
        super(new SimpleDispatcher<>(), 2000L);
    }
    public synchronized long post(Runnable task) {
        return postAtTime(task, 0, SystemClock.elapsedRealtime());
    }

    public synchronized long post(Runnable task, int priority) {
        return postAtTime(task, priority, SystemClock.elapsedRealtime());
    }

    public synchronized long postDelay(Runnable task, long delay) {
        return postAtTime(task, 0, SystemClock.elapsedRealtime() + delay);
    }

    public synchronized long postDelay(Runnable task, int priority, long delay) {
        return postAtTime(task, priority, SystemClock.elapsedRealtime() + delay);
    }

    public synchronized long postAtTime(Runnable task, int priority, long uptimeMillis) {
        return super.post(new SimpleTaskParam(priority, uptimeMillis, mainHandler), task);
    }

}
