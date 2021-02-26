package com.zpf.tool.stack;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Activity 栈管理
 * Created by ZPF on 2019/1/12.
 */
public class AppStackUtil implements Application.ActivityLifecycleCallbacks {
    private int topStackState = LifecycleState.NOT_INIT;
    private final HashStack<String, IStackItem> stackInfo = new HashStack<>();
    public static final String STACK_ITEM_NAME = "stack_item_name";

    private AppStackUtil() {
    }

    private static class Instance {
        static AppStackUtil mInstance = new AppStackUtil();
    }

    public static AppStackUtil get() {
        return Instance.mInstance;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        String stackName = getNameInStack(activity);
        IStackItem targetItem;
        IStackItem record = stackInfo.get(stackName);
        if (record == null) {
            targetItem = new ActivityStackItem(stackName);
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
    public void onActivityStarted(@NonNull Activity activity) {
        checkActivityInStackTop(activity);
        topStackState = LifecycleState.AFTER_START;
        activity.getIntent().putExtra("onActivitySaveInstanceState", false);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        checkActivityInStackTop(activity);
        topStackState = LifecycleState.AFTER_RESUME;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        checkActivityInStackTop(activity);
        topStackState = LifecycleState.AFTER_PAUSE;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (getTopActivity() == activity) {
            topStackState = LifecycleState.AFTER_STOP;
        }
    }

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        activity.getIntent().putExtra("onActivitySaveInstanceState", true);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        String stackName = getNameInStack(activity);
        if (getTopActivity() == activity) {
            topStackState = LifecycleState.AFTER_DESTROY;
            stackInfo.remove(stackName);
        } else {
            if (activity.getIntent().getBooleanExtra("onActivitySaveInstanceState", false)) {
                IStackItem record = stackInfo.get(stackName);
                if (record != null) {
                    record.setItemState(StackElementState.STACK_REMOVING);
                }
            } else {
                stackInfo.remove(stackName);
            }
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

    public boolean finishByName(String stackItemName) {
        IStackItem record = stackInfo.get(stackItemName);
        if (record == null) {
            return false;
        }
        Activity activity = record.getStackActivity();
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        } else {
            record.setItemState(StackElementState.STACK_REMOVING);
        }
        return true;
    }

    /**
     * 从历史栈中回退到指定活动
     * 如果活动在历史中则，结束在目标活动之上的所有活动
     * 如果活动不在历史中，则将结束历史中的每个活动，直到到达该历史栈的根活动
     *
     * @return 目标活动之下的活动数量，如果活动不在历史中返回-1
     */
    public int finishAboveName(String stackItemName) {
        IStackItem item = stackInfo.pollLast();
        int result = -1;
        while (item != null) {
            if (TextUtils.equals(item.getName(), stackItemName)) {
                stackInfo.put(stackItemName, item);
                result = stackInfo.getSize() - 1;
                break;
            }
            if (stackInfo.getSize() == 0) {
                stackInfo.put(stackItemName, item);
                result = -1;
                break;
            }
            Activity activity = item.getStackActivity();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            } else {
                item.setItemState(StackElementState.STACK_REMOVING);
            }
            item = stackInfo.pollLast();
        }
        return result;
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

    public String getNameInStack(Activity activity) {
        String stackName = activity.getIntent().getStringExtra(STACK_ITEM_NAME);
        if (stackName == null || stackName.length() == 0) {
            stackName = activity.getClass().getName();
        }
        return stackName;
    }

    private void checkActivityInStackTop(Activity activity) {
        String stackName = getNameInStack(activity);
        IStackItem targetItem;
        IStackItem record = stackInfo.get(stackName);
        if (record == null) {
            targetItem = new ActivityStackItem(stackName);
            targetItem.bindActivity(activity);
            stackInfo.put(targetItem.getName(), targetItem);
        } else {
            stackInfo.moveAllNext(stackName);
        }
    }

}
