package com.zpf.tool.stack;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Activity 栈管理
 * Created by ZPF on 2019/1/12.
 */
public class AppStackUtil {
    public static final String STACK_ITEM_NAME = "stack_item_name";
    private static int topStackState = LifecycleState.NOT_INIT;
    private static final HashStack<String, IStackItem> stackInfo = new HashStack<>();
    private static final ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
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
            if (savedInstanceState != null && targetItem.getItemState() == StackElementState.STACK_REMOVING) {
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
                        record.setItemState(StackElementState.STACK_OUTSIDE);
                    }
                } else {
                    stackInfo.remove(stackName);
                }
            }
        }
    };

    public static void init(Application application) {
        if (application != null) {
            application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
        }
    }

    @StackElementState
    public static int getStackItemState(String name) {
        IStackItem stackItem = stackInfo.get(name);
        if (stackItem == null) {
            return StackElementState.STACK_OUTSIDE;
        } else {
            return stackItem.getItemState();
        }
    }

    public static int getStackSize() {
        return stackInfo.getSize();
    }

    @LifecycleState
    public static int getTopState() {
        return topStackState;
    }

    @Nullable
    public static Activity getTopActivity() {
        IStackItem topItem = getTopStackInfo();
        if (topItem == null) {
            return null;
        } else {
            return topItem.getStackActivity();
        }
    }

    public static IStackItem getTopStackInfo() {
        return stackInfo.getLast();
    }

    public static boolean finishByName(String stackItemName) {
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
     * @return 如果活动不在历史中返回false
     */
    public static boolean finishUntilName(String stackItemName) {
        if (stackItemName == null || stackItemName.length() == 0) {
            return false;
        }
        IStackItem itemInStack = stackInfo.get(stackItemName);
        boolean result = itemInStack != null;
        StackNode<String, IStackItem> itemNode = stackInfo.getLastNode();
        while (itemNode != null) {
            if (result && itemInStack == itemNode.item) {
                break;
            }
            if (itemNode.prev == null) {
                break;
            }
            Activity activity = itemNode.item.getStackActivity();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            } else {
                itemNode.item.setItemState(StackElementState.STACK_REMOVING);
            }
            itemNode = itemNode.prev;
        }
        return result;
    }

    public static void finishExceptTop() {
        StackNode<String, IStackItem> lastNode = stackInfo.getLastNode();
        if (lastNode == null) {
            return;
        }
        lastNode = lastNode.prev;
        while (lastNode != null) {
            Activity activity = lastNode.item.getStackActivity();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            } else {
                lastNode.item.setItemState(StackElementState.STACK_REMOVING);
            }
            lastNode = lastNode.prev;
        }
    }

    public static void clear() {
        StackNode<String, IStackItem> node = stackInfo.getLastNode();
        while (node != null) {
            if (node.item.getStackActivity() != null) {
                node.item.getStackActivity().finish();
            } else {
                node.item.setItemState(StackElementState.STACK_REMOVING);
            }
            node = node.prev;
        }
        topStackState = LifecycleState.NOT_INIT;
    }

    public static String getNameInStack(Activity activity) {
        String stackName = activity.getIntent().getStringExtra(STACK_ITEM_NAME);
        if (stackName == null || stackName.length() == 0) {
            stackName = activity.getClass().getName();
        }
        return stackName;
    }

    private static void checkActivityInStackTop(Activity activity) {
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