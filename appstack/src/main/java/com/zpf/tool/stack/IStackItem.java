package com.zpf.tool.stack;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IStackItem {

    @NonNull
    String getName();

    @StackElementState
    int getItemState();

    void setItemState(@StackElementState int newState);

    void bindActivity(Activity activity);

    @Nullable
    Activity getStackActivity();

}
