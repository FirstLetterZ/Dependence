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

    private AppStackUtil() {
    }

    private static class Instance {
        private static final AppStackUtil mInstance = new AppStackUtil();
    }

    private final ActivityStackManager stackManager = new ActivityStackManager();
    private final ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            String stackName = stackManager.getNameInStack(activity);
            ActivityStackItem stackItem = stackManager.search(stackName);
            if (savedInstanceState != null && stackItem != null && stackItem.getItemState() == StackElementState.STACK_REMOVING) {
                activity.finish();
                return;
            }
            stackManager.moveToStackTop(activity);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            stackManager.moveToStackTop(activity);
            activity.getIntent().putExtra("onActivitySaveInstanceState", false);
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            stackManager.moveToStackTop(activity);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            activity.getIntent().putExtra("onActivitySaveInstanceState", true);
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            activity.getIntent().putExtra("onActivitySaveInstanceState", true);
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            String stackName = stackManager.getNameInStack(activity);
            ActivityStackItem stackItem = stackManager.search(stackName);
            if (stackItem == null) {
                return;
            }
            ActivityStackItem topItem = stackManager.peek();
            if (activity.getIntent().getBooleanExtra("onActivitySaveInstanceState", false)) {
                if (stackItem != null) {
                    stackItem.setItemState(StackElementState.STACK_OUTSIDE);
                }
                return;
            }
            if (topItem != null && topItem.getStackItem() == activity) {
                stackManager.pop();
            } else {
                stackManager.remove(stackName);
            }
        }
    };

    public static void init(Application application) {
        if (application != null) {
            application.registerActivityLifecycleCallbacks(Instance.mInstance.lifecycleCallbacks);
        }
    }

    public static int size() {
        return Instance.mInstance.stackManager.size();
    }

    @Nullable
    public static Activity getTopActivity() {
        ActivityStackItem topItem = Instance.mInstance.stackManager.peek();
        if (topItem == null) {
            return null;
        } else {
            return topItem.getStackItem();
        }
    }

    public static ActivityStackItem search(String name) {
        return Instance.mInstance.stackManager.search(name);
    }

    public static boolean finishByName(String stackItemName) {
        return Instance.mInstance.stackManager.remove(stackItemName);
    }

    /**
     * 从历史栈中回退到指定活动
     * 如果活动在历史中则，结束在目标活动之上的所有活动
     * 如果活动不在历史中，则将结束历史中的每个活动，直到到达该历史栈的根活动
     *
     * @return 如果活动不在历史中返回false
     */
    public static boolean finishUntil(String stackItemName) {
        return Instance.mInstance.stackManager.popTo(stackItemName);
    }

    public static void finishToRoot() {
        Instance.mInstance.stackManager.popToRoot();
    }

    public static void clear(boolean keepTop) {
        Instance.mInstance.stackManager.clear(keepTop);
    }

    public static String getNameInStack(Activity activity) {
        if (activity == null) {
            return null;
        }
        return Instance.mInstance.stackManager.getNameInStack(activity);
    }
}