package com.zpf.tool.fragment;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class PendingNode<T> {
    private final WeakReference<T> value;
    public final int parent;
    public final String tag;

    public PendingNode(@NonNull T value, int parent, @NonNull String tag) {
        this.value = new WeakReference<>(value);
        this.parent = parent;
        this.tag = tag;
    }

    public T getValue() {
        return value.get();
    }
}