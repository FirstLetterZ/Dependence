package com.zpf.update.task;

import android.os.Handler;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicLong;

public class SimpleDispatcher<T> implements IDispatcher<T> {
    private final AtomicLong nextTaskDelayTime = new AtomicLong();

    @Override
    public boolean intercept(@Nullable T param) {
        return false;
    }

    @Override
    public boolean apply(@Nullable T param, int index) {
        if (index == 0) {
            nextTaskDelayTime.set(0L);
        }
        long when;
        if (param instanceof IParamWhen) {
            when = ((IParamWhen) param).when();
        } else {
            when = 0L;
        }
        long delay = when - timestamp();
        if (delay > 0L && (nextTaskDelayTime.get() > delay || nextTaskDelayTime.get() <= 0)) {
            nextTaskDelayTime.set(delay);
        }
        return delay <= 0;
    }

    @Override
    public void dispatch(@Nullable T param, @NonNull Runnable task) {
        if (param instanceof IParamHandler) {
            Handler handler = ((IParamHandler) param).getTaskHandler();
            if (handler != null) {
                handler.post(task);
                return;
            }
        }
        task.run();
    }

    @Override
    public long nextInterval() {
        return nextTaskDelayTime.get();
    }

    public long timestamp() {
        return SystemClock.elapsedRealtime();
    }
}
