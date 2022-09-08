package com.zpf.tool.stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class LifecycleStackItem<T> implements IStackItem<T>, NodeStateListener {
    protected final String name;
    protected WeakReference<T> mInstance;
    @LifecycleState
    protected int lifecycleState = LifecycleState.NOT_INIT;
    protected NodeStateListener nodeStateListener;

    public LifecycleStackItem(String name) {
        if (name == null) {
            throw new IllegalArgumentException("stack name cannot be null!");
        }
        this.name = name;
    }

    @NonNull
    @Override
    public String getKey() {
        return name;
    }

    @Override
    public void update(T value) {
        mInstance = new WeakReference<>(value);
    }

    @Nullable
    @Override
    public T getValue() {
        if (mInstance == null) {
            return null;
        }
        return mInstance.get();
    }

    @LifecycleState
    public int getState() {
        return lifecycleState;
    }

    public void setState(@LifecycleState int lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public void setNodeStateListener(NodeStateListener nodeStateListener) {
        this.nodeStateListener = nodeStateListener;
    }

    @Override
    public void onStateChanged(boolean inStack) {
        if (this.nodeStateListener != null) {
            this.nodeStateListener.onStateChanged(inStack);
        }
    }
}