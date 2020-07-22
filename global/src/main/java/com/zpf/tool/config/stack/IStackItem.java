package com.zpf.tool.config.stack;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;

/**
 * 不要使用context继承，context应使用 com.zpf.tool.config.stack.IStackItemPrototype
 * getStackActivity所返回的activity不要使用强引用
 */
public interface IStackItem {

    @NonNull
    String getName();

    @StackElementState
    int getItemState();

    void setItemState(@StackElementState int newState);

    void bindActivity(Activity activity);

    @Nullable
    Activity getStackActivity();

    @Nullable
    LinkedList<IStackItem> getInsideStack();
}
