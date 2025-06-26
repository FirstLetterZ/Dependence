package com.zpf.update.task;

import android.os.Handler;

import androidx.annotation.Nullable;

public class SimpleTaskParam implements Comparable<SimpleTaskParam>, IParamHandler, IParamWhen {
    public final int priority;
    public final long when;
    @Nullable
    public final Handler handler;

    public SimpleTaskParam() {
        this.priority = 0;
        this.when = 0L;
        this.handler = null;
    }

    public SimpleTaskParam(int priority) {
        this.priority = priority;
        this.when = 0L;
        this.handler = null;
    }

    public SimpleTaskParam(long when, int priority) {
        this.priority = priority;
        this.when = when;
        this.handler = null;
    }

    public SimpleTaskParam(int priority, long when, @Nullable Handler handler) {
        this.priority = priority;
        this.when = when;
        this.handler = handler;
    }

    @Nullable
    @Override
    public Handler getTaskHandler() {
        return handler;
    }
    @Override
    public long when() {
        return when;
    }
    @Override
    public int compareTo(SimpleTaskParam other) {
        if (other == null) {
            return -1;
        }
        if (priority > other.priority) {
            return -1;
        }
        if (priority < other.priority) {
            return 1;
        }
        return Long.compare(when(), other.when());
    }
}
