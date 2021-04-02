package com.zpf.tool.stack;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class ActivityStackItem implements IStackItem<Activity> {

    private final String name;
    private int elementState = StackElementState.STACK_OUTSIDE;
    private WeakReference<Activity> mInstance;

    public ActivityStackItem(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    @StackElementState
    public int getItemState() {
        return elementState;
    }

    @Override
    public void setItemState(@StackElementState int newState) {
        elementState = newState;
    }

    @Override
    public void bindItem(Activity activity) {
        mInstance = new WeakReference<>(activity);
    }

    @Nullable
    @Override
    public Activity getStackItem() {
        if (mInstance == null) {
            return null;
        }
        return mInstance.get();
    }

}