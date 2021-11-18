package com.zpf.tool.stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IStackItem<T> {

    @NonNull
    String getKey();

    void update(T t);

    @Nullable
    T getValue();

}