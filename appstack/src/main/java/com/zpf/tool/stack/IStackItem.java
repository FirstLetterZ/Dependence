package com.zpf.tool.stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IStackItem<T> {

    @NonNull
    String getName();

    @StackElementState
    int getItemState();

    void setItemState(@StackElementState int newState);

    void bindItem(T t);

    @Nullable
    T getStackItem();

}
