package com.zpf.tool.stack;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Activity 栈管理
 * Created by ZPF on 2019/1/12.
 */
public class AppStackUtil {
    public static final String STACK_ITEM_NAME = "stack_item_name";
    public static final String STACK_ITEM_RECYCLED = "stack_item_recycled";

    private static class Instance {
        private static final AppStackUtil mInstance = new AppStackUtil();
    }

    private AppStackUtil() {
    }

    private final HashMap<String, Boolean> recycledStackItem = new HashMap<>();
    private final HashStack<String, ActivityStackItem> stackInfo = new HashStack<>();

    public final Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            String stackName = AppStackUtil.getNameInStack(activity);
            Boolean shouldFinish = recycledStackItem.remove(stackName);
            if (savedInstanceState != null && shouldFinish != null && shouldFinish) {
                activity.finish();
                return;
            }
            updateShowingStackItem(activity, LifecycleState.AFTER_CREATE);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            updateShowingStackItem(activity, LifecycleState.AFTER_START);
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            updateShowingStackItem(activity, LifecycleState.AFTER_RESUME);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            updateHiddenStackItem(activity, LifecycleState.AFTER_PAUSE);
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            updateHiddenStackItem(activity, LifecycleState.AFTER_STOP);
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            activity.getIntent().putExtra(STACK_ITEM_RECYCLED, true);
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            String stackName = getNameInStack(activity);
            ActivityStackItem stackItem = stackInfo.get(stackName);
            if (stackItem == null || stackItem.getValue() != activity) {
                return;
            }
            stackItem.setState(LifecycleState.AFTER_DESTROY);
            stackItem.update(null);
            if (stackInfo.getLast() == stackItem ||
                    !activity.getIntent().getBooleanExtra(STACK_ITEM_RECYCLED, false)) {
                stackInfo.remove(stackName);
            } else {
                recycledStackItem.put(stackName, false);
            }
        }
    };

    public static void init(Application application) {
        if (application != null) {
            application.registerActivityLifecycleCallbacks(Instance.mInstance.lifecycleCallbacks);
        }
    }

    public static int size() {
        return Instance.mInstance.stackInfo.size();
    }

    @Nullable
    public static Activity getTopActivity() {
        ActivityStackItem topItem = Instance.mInstance.stackInfo.getLast();
        if (topItem == null) {
            return null;
        } else {
            return topItem.getValue();
        }
    }

    public static ActivityStackItem search(String name) {
        return Instance.mInstance.stackInfo.get(name);
    }

    public static boolean finishByName(String stackItemName) {
        ActivityStackItem stackItem = Instance.mInstance.stackInfo.remove(stackItemName);
        if (stackItem == null) {
            Boolean shouldFinish = Instance.mInstance.recycledStackItem.get(stackItemName);
            if (shouldFinish != null) {
                Instance.mInstance.recycledStackItem.put(stackItemName, true);
            }
            return false;
        }
        return true;
    }

    public static boolean finishUntil(String stackItemName) {
        boolean result = Instance.mInstance.stackInfo.removeAfter(stackItemName);
        if (Instance.mInstance.recycledStackItem.size() > 0) {
            Iterator<Map.Entry<String, Boolean>> iterator = Instance.mInstance.recycledStackItem.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Boolean> entry = iterator.next();
                if (!Instance.mInstance.stackInfo.has(entry.getKey())) {
                    iterator.remove();
                }
            }
        }
        return result;
    }

    public static void finishToRoot() {
        ActivityStackItem stackItem = Instance.mInstance.stackInfo.getFirst();
        if (stackItem != null) {
            Instance.mInstance.recycledStackItem.clear();
            Instance.mInstance.stackInfo.removeAfter(stackItem.getKey());
        }
    }

    public static void clear(boolean keepTop) {
        Instance.mInstance.recycledStackItem.clear();
        ActivityStackItem stackItem = Instance.mInstance.stackInfo.getLast();
        if (keepTop) {
            Instance.mInstance.stackInfo.removeBefore(stackItem.getKey());
        } else {
            Instance.mInstance.stackInfo.clear();
        }
    }

    public static String getNameInStack(Object obj) {
        if (obj == null) {
            return null;
        }
        String stackName = null;
        StackItem annotation;
        if (obj instanceof Class<?>) {
            annotation = ((Class<?>) obj).getAnnotation(StackItem.class);
        } else {
            annotation = obj.getClass().getAnnotation(StackItem.class);
        }
        if (annotation != null) {
            stackName = annotation.name();
        }
        if (stackName != null) {
            return stackName;
        }
        if (obj instanceof Activity) {
            stackName = ((Activity) obj).getIntent().getStringExtra(STACK_ITEM_NAME);
        }
        if (stackName != null) {
            return stackName;
        }
        stackName = obj.getClass().getName();
        return stackName;
    }

    private void updateShowingStackItem(Activity activity, int state) {
        activity.getIntent().removeExtra(STACK_ITEM_RECYCLED);
        String stackName = getNameInStack(activity);
        ActivityStackItem stackItem = stackInfo.get(stackName);
        if (stackItem == null) {
            stackItem = new ActivityStackItem(stackName);
        }
        stackItem.update(activity);
        stackInfo.add(stackName, stackItem);
        stackItem.setState(state);
    }

    private void updateHiddenStackItem(Activity activity, int state) {
        String stackName = getNameInStack(activity);
        ActivityStackItem stackItem = stackInfo.get(stackName);
        if (stackItem != null && stackItem.getValue() == activity) {
            stackItem.setState(state);
        }
    }
}