package com.zpf.tool.config.stack;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class ActivityStackItem implements IStackItem{

    private String name;
    private int elementState = StackElementState.STACK_OUTSIDE;
    private WeakReference<Activity> mInstance;

    public void init(Activity activity) {
        name = activity.getClass().getName();
        mInstance = new WeakReference<>(activity);
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

    @Nullable
    @Override
    public Activity getStackActivity() {
        if (mInstance == null) {
            return null;
        }
        return mInstance.get();
    }

    @Nullable
    @Override
    public LinkedList<IStackItem> getInsideStack() {
        return null;
    }
}
