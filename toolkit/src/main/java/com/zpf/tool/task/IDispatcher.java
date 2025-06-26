package com.zpf.tool.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IDispatcher<T> {

    boolean intercept(@Nullable T t);

    boolean apply(@Nullable T t, int index);

    void dispatch(@Nullable T t, @NonNull Runnable task);

    long nextInterval();
}
