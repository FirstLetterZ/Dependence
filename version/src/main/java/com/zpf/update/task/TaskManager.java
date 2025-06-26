package com.zpf.update.task;

import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class TaskManager<P extends Comparable<P>> {
    private final TaskQueue<P> queue = new TaskQueue<>();
    private final IDispatcher<P> dispatcher;
    private final Object lock = new Object();
    private final long aliveTime;
    private final AtomicBoolean finishAtNext = new AtomicBoolean();
    private volatile Thread workThread = null;

    public TaskManager(SimpleDispatcher<P> dispatcher, long aliveTime) {
        this.dispatcher = dispatcher;
        this.aliveTime = Math.min(aliveTime, 20L);
    }

    public long post(@Nullable P param, @Nullable Runnable runnable) {
        if (runnable == null) {
            return 0;
        }
        if (dispatcher.intercept(param)) {
            return 0;
        }
        return queue.add(param, runnable);
    }

    public boolean remove(long id) {
        return queue.remove(id);
    }

    public boolean remove(Runnable task) {
        return queue.remove(task);
    }
    public boolean next() {
        if (queue.isEmpty()) {
            return false;
        }
        notifyWorkThread();
        return true;
    }

    public synchronized void quite() {
        queue.clear();
        if (workThread != null && workThread.isAlive()) {
            workThread.interrupt();
        }
    }

    private void notifyWorkThread() {
        if (workThread == null || !workThread.isAlive()) {
            synchronized (this) {
                if (workThread == null || !workThread.isAlive()) {
                    workThread = createThread();
                    workThread.start();
                } else {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            }
        } else {
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    private Thread createThread() {
        return new Thread(() -> {
            finishAtNext.set(false);
            while (true) {
                try {
                    boolean takeOne = queue.take(dispatcher);
                    long nextDelay;
                    if (takeOne) {
                        finishAtNext.set(false);
                    }
                    synchronized (queue) {
                        if (queue.isEmpty()) {
                            if (finishAtNext.get()) {
                                break;
                            } else {
                                finishAtNext.set(true);
                                nextDelay = aliveTime;
                            }
                        } else if (takeOne) {
                            nextDelay = 0L;
                        } else {
                            nextDelay = dispatcher.nextInterval();
                        }
                    }
                    if (nextDelay >= 0L) {
                        synchronized (lock) {
                            lock.wait(nextDelay);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
    }

}