package com.zpf.tool.config;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Activity 栈管理
 * Created by ZPF on 2019/1/12.
 */
public class AppStackUtil implements Application.ActivityLifecycleCallbacks {
    private final LinkedList<WeakReference<Activity>> stackInfoList = new LinkedList<>();
    private int topStackState;
    private WeakReference<Activity> topStackActivity;
    private static volatile  AppStackUtil mInstance;

    private AppStackUtil() {

    }

    public static AppStackUtil get() {
        if (mInstance == null) {
            synchronized (AppStackUtil.class) {
                if (mInstance == null) {
                    mInstance = new AppStackUtil();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        stackInfoList.addFirst(weakReference);
        topStackState = LifecycleState.AFTER_CREATE;
        topStackActivity = weakReference;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        checkActivityInStack(activity);
        topStackState = LifecycleState.AFTER_START;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        checkActivityInStack(activity);
        topStackState = LifecycleState.AFTER_RESUME;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        checkActivityInStack(activity);
        topStackState = LifecycleState.AFTER_START;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (topStackActivity != null && topStackActivity.get() == activity) {
            topStackState = LifecycleState.AFTER_STOP;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (topStackActivity != null && topStackActivity.get() == activity) {
            topStackState = LifecycleState.AFTER_DESTROY;
        }
        removeActivity(activity);
    }

    private void removeActivity(Activity activity) {
        synchronized (stackInfoList) {
            LinkedList<WeakReference<Activity>> tempList = null;
            WeakReference<Activity> destroyedActivity;
            while ((destroyedActivity = stackInfoList.pollLast()) != null) {
                if (destroyedActivity.get() == activity) {
                    break;
                } else {
                    if (tempList == null) {
                        tempList = new LinkedList<>();
                    }
                    tempList.addFirst(destroyedActivity);
                }
            }
            if (tempList == null || tempList.size() > 0) {
                stackInfoList.addAll(0, tempList);
            }
        }
    }

    private void checkActivityInStack(Activity activity) {
        synchronized (stackInfoList) {
            boolean hasAdd = false;
            for (WeakReference<Activity> weakReference : stackInfoList) {
                if (weakReference != null && weakReference.get() == activity) {
                    hasAdd = true;
                    topStackActivity = weakReference;
                    break;
                }
            }
            if (!hasAdd) {
                WeakReference<Activity> realTopActivity = new WeakReference<Activity>(activity);
                stackInfoList.addFirst(realTopActivity);
                topStackActivity = realTopActivity;
            }
        }
    }

    @LifecycleState
    public int getTopActivityState() {
        return topStackState;
    }

    public Activity getTopActivity() {
        if (topStackActivity == null) {
            return null;
        } else {
            return topStackActivity.get();
        }
    }
}
