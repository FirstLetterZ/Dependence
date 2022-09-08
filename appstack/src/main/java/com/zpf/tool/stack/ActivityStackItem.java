package com.zpf.tool.stack;

import android.app.Activity;

public class ActivityStackItem extends LifecycleStackItem<Activity> {

    public ActivityStackItem(String name) {
        super(name);
    }

    @Override
    public void onStateChanged(boolean inStack) {
        if (!inStack && getState() < LifecycleState.AFTER_DESTROY) {
            Activity activity = getValue();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        super.onStateChanged(inStack);
    }
}