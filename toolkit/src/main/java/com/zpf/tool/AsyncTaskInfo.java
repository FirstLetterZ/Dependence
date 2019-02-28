package com.zpf.tool;

import com.zpf.tool.config.MainHandler;

/**
 * Created by ZPF on 2018/10/23.
 */
public abstract class AsyncTaskInfo<T> implements Runnable {
    public static final int LEVEL_HEIGHT = 0;
    public static final int LEVEL_NORMAL = 1;
    public static final int LEVEL_LOW = 2;

    private boolean onMainTread = true;

    public AsyncTaskInfo() {
    }

    public AsyncTaskInfo(boolean onMainTread) {
        this.onMainTread = onMainTread;
    }

    @Override
    public final void run() {
        final T result = task();
        if (onMainTread) {
            MainHandler.get().post(new Runnable() {
                @Override
                public void run() {
                    complete(result);
                }
            });
        } else {
            complete(result);
        }
    }

    public abstract T task();

    protected void complete(T t) {

    }
}
