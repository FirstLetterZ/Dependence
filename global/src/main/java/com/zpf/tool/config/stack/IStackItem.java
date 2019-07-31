package com.zpf.tool.config.stack;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedList;

/**
 * 不要使用context继承，context应使用{@link IStackItemPrototype}
 * getStackActivity所返回的activity不要使用强引用
 */
public interface IStackItem {

    @NonNull
    String getName();

    @StackElementState
    int getItemState();

    void setItemState(@StackElementState int newState);

    @Nullable
    Activity getStackActivity();

    @Nullable
    LinkedList<IStackItem> getInsideStack();
}
