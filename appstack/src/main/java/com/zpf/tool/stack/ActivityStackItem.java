package com.zpf.tool.stack;

import android.app.Activity;

public class ActivityStackItem extends LifecycleStackItem<Activity> {

    public ActivityStackItem(String name) {
        super(name);
        this.nodeStateListener = new HashStack.NodeStateListener() {
            @Override
            public void onStateChanged(boolean inStack) {
                if (!inStack && mInstance != null) {
                    Activity activity = mInstance.get();
                    if (activity != null && !activity.isFinishing()) {
                        activity.finish();
                    }
                }
            }
        };
    }
}