package com.zpf.apptest.task;

import androidx.annotation.Nullable;

public interface IRequestJob<T> {

    void cancel();

    boolean isRunning();

    boolean isCanceled();

    boolean isSuccess();

    @Nullable
    IResponse<T> response();
}
