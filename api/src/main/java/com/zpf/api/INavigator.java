package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZPF on 2019/5/13.
 */
public interface INavigator<T, I, O> {

    void push(@NonNull T target, @Nullable I params, @NonNull IDataCallback<O> callback);

    void push(@NonNull T target, @Nullable I params);

    void push(@NonNull T target);

    void pop(int code, @Nullable O data);

    void pop();

    void popToRoot(@Nullable O data);

    boolean popTo(@NonNull T target, @Nullable O data);

    void replace(@NonNull T target, @Nullable I params);

    boolean remove(@NonNull T target);
}
