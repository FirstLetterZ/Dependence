package com.zpf.tool;

import android.support.annotation.IntRange;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ZPF on 2018/10/23.
 */
public class AsyncTaskQueue {
    private static volatile AsyncTaskQueue mInstance;
    private volatile AsyncTaskInfo doingTask;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final TaskArray taskArray = new TaskArray();
    private final Runnable taskRunnable = new Runnable() {
        @Override
        public void run() {
            while (doingTask != null) {
                doingTask.run();
                doingTask = taskArray.pollTask();
            }
        }
    };

    private AsyncTaskQueue() {
    }

    public static AsyncTaskQueue get() {
        if (mInstance == null) {
            synchronized (AsyncTaskQueue.class) {
                if (mInstance == null) {
                    mInstance = new AsyncTaskQueue();
                }
            }
        }
        return mInstance;
    }

    public void startTask(AsyncTaskInfo runnable) {
        startTask(runnable, AsyncTaskInfo.LEVEL_NORMAL);
    }

    public void startTask(AsyncTaskInfo runnable, @IntRange(from = 0, to = 2) int taskLevel) {
        if (taskArray.addTask(runnable, taskLevel)) {
            checkTaskStart();
        }
    }

    private synchronized void checkTaskStart() {
        if (doingTask == null) {
            synchronized (AsyncTaskQueue.class) {
                if (doingTask == null) {
                    doingTask = taskArray.pollTask();
                }
            }
            if (doingTask != null) {
                executorService.execute(taskRunnable);
            }
        }
    }

    private class TaskArray {
        private LinkedList<AsyncTaskInfo> heightLevelTaskList = new LinkedList<>();
        private LinkedList<AsyncTaskInfo> normalLevelTaskList = new LinkedList<>();
        private LinkedList<AsyncTaskInfo> lowLevelTaskList = new LinkedList<>();

        private boolean addTask(AsyncTaskInfo runnable, @IntRange(from = 0, to = 2) int taskLevel) {
            switch (taskLevel) {
                case AsyncTaskInfo.LEVEL_HEIGHT:
                    heightLevelTaskList.add(runnable);
                    break;
                case AsyncTaskInfo.LEVEL_NORMAL:
                    normalLevelTaskList.add(runnable);
                    break;
                case AsyncTaskInfo.LEVEL_LOW:
                    lowLevelTaskList.add(runnable);
                    break;
                default:
                    return false;
            }
            return true;
        }

        private AsyncTaskInfo pollTask() {
            AsyncTaskInfo task = heightLevelTaskList.pollFirst();
            if (task != null) {
                return task;
            }
            task = normalLevelTaskList.pollFirst();
            if (task != null) {
                return task;
            }
            task = lowLevelTaskList.pollFirst();
            return task;
        }
    }
}
