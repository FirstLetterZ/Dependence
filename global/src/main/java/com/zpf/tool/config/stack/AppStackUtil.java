package com.zpf.tool.config.stack;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.zpf.tool.config.LifecycleState;

/**
 * Activity 栈管理
 * Created by ZPF on 2019/1/12.
 */
public class AppStackUtil implements Application.ActivityLifecycleCallbacks {
    private int topStackState = LifecycleState.NOT_INIT;
    private HashStack<String, IStackItem> stackInfo = new HashStack<>();

    private AppStackUtil() {
    }

    private static class Instance {
        static AppStackUtil mInstance = new AppStackUtil();
    }

    public static AppStackUtil get() {
        return Instance.mInstance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        String stackName;
        IStackItem targetItem = null;
        if (activity instanceof IStackItemPrototype) {
            targetItem = ((IStackItemPrototype) activity).getStackItem();
            stackName = targetItem.getName();
        } else {
            stackName = activity.getClass().getName();
        }
        IStackItem record = stackInfo.get(stackName);
        if (record == null) {
            if (targetItem == null) {
                targetItem = new ActivityStackItem();
            }
            stackInfo.put(targetItem.getName(), targetItem);
        } else {
            targetItem = record;
        }
        if (targetItem.getItemState() == StackElementState.STACK_REMOVING) {
            activity.finish();
        } else {
            targetItem.bindActivity(activity);
            targetItem.setItemState(StackElementState.STACK_TOP);
            topStackState = LifecycleState.AFTER_CREATE;
            stackInfo.moveAllNext(stackName);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        checkActivityInStack(activity);
        topStackState = LifecycleState.AFTER_START;
        activity.getIntent().putExtra("onActivitySaveInstanceState", false);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        checkActivityInStack(activity);
        topStackState = LifecycleState.AFTER_RESUME;
        activity.getIntent().putExtra("onActivitySaveInstanceState", false);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        checkActivityInStack(activity);
        topStackState = LifecycleState.AFTER_PAUSE;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (getTopActivity() == activity) {
            topStackState = LifecycleState.AFTER_STOP;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        activity.getIntent().putExtra("onActivitySaveInstanceState", outState != null);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (getTopActivity() == activity) {
            topStackState = LifecycleState.AFTER_DESTROY;
        }
        if (!activity.getIntent().getBooleanExtra("onActivitySaveInstanceState", false)) {
            removeActivity(activity);
        }
    }

    private void removeActivity(Activity activity) {
        String stackName;
        if (activity instanceof IStackItemPrototype) {
            stackName = ((IStackItemPrototype) activity).getStackItem().getName();
        } else {
            stackName = activity.getClass().getName();
        }
        stackInfo.remove(stackName);
    }

    private void checkActivityInStack(Activity activity) {
        String stackName;
        IStackItem targetItem = null;
        if (activity instanceof IStackItemPrototype) {
            targetItem = ((IStackItemPrototype) activity).getStackItem();
            stackName = targetItem.getName();
        } else {
            stackName = activity.getClass().getName();
        }
        IStackItem record = stackInfo.get(stackName);
        if (record == null) {
            if (targetItem == null) {
                targetItem = new ActivityStackItem();
                targetItem.bindActivity(activity);
            }
            stackInfo.put(targetItem.getName(), targetItem);
        } else {
            stackInfo.moveAllNext(stackName);
        }
    }

    @StackElementState
    public int getStackItemState(String name) {
        IStackItem stackItem = stackInfo.get(name);
        if (stackItem == null) {
            return StackElementState.STACK_OUTSIDE;
        } else {
            return stackItem.getItemState();
        }
    }

    public int getStackSize() {
        return stackInfo.getSize();
    }

    @LifecycleState
    public int getTopActivityState() {
        return topStackState;
    }

    @Nullable
    public Activity getTopActivity() {
        IStackItem topItem = getTopStackInfo();
        if (topItem == null) {
            return null;
        } else {
            return topItem.getStackActivity();
        }
    }

    public IStackItem getTopStackInfo() {
        return stackInfo.getLast();
    }

    public boolean finishAbove(Activity activity) {
        String stackName;
        if (activity instanceof IStackItemPrototype) {
            IStackItem targetItem = ((IStackItemPrototype) activity).getStackItem();
            stackName = targetItem.getName();
        } else {
            stackName = activity.getClass().getName();
        }
        IStackItem record = stackInfo.get(stackName);
        if (record == null) {
            return false;
        } else {
            IStackItem item = stackInfo.getLast();
            while (item != null) {
                if (TextUtils.equals(item.getName(), stackName)) {
                    stackInfo.put(stackName, item);
                } else if (item.getStackActivity() != null) {
                    item.getStackActivity().finish();
                }
                item = stackInfo.pollLast();
            }
            return true;
        }
    }

    public void clear() {
        IStackItem item = stackInfo.pollFirst();
        while (item != null) {
            if (item.getStackActivity() != null) {
                item.getStackActivity().finish();
            }
            item = stackInfo.pollFirst();
        }
        topStackState = LifecycleState.NOT_INIT;
    }
}
