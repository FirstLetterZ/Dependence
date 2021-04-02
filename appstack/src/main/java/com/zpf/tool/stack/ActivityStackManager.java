package com.zpf.tool.stack;

import android.app.Activity;

/**
 * @author Created by ZPF on 2021/4/2.
 */
public class ActivityStackManager implements IStackManager<ActivityStackItem> {
    private final HashStack<String, ActivityStackItem> stackInfo = new HashStack<>();

    @Override
    public void push(ActivityStackItem activityStackItem) {
        stackInfo.put(activityStackItem.getName(), activityStackItem);
    }

    @Override
    public void pop() {
        stackInfo.pollLast();
    }

    /**
     * 从历史栈中回退到指定活动
     * 如果活动在历史中则，结束在目标活动之上的所有活动
     * 如果活动不在历史中，则将结束历史中的每个活动，直到到达该历史栈的根活动
     *
     * @return 如果活动不在历史中返回false
     */
    @Override
    public boolean popTo(String name) {
        if (name == null || name.length() == 0) {
            return false;
        }
        ActivityStackItem itemInStack = stackInfo.get(name);
        boolean result = itemInStack != null;
        StackNode<String, ActivityStackItem> itemNode = stackInfo.getLastNode();
        while (itemNode != null) {
            if (result && itemInStack == itemNode.item) {
                break;
            }
            if (itemNode.prev == null) {
                break;
            }
            Activity activity = itemNode.item.getStackItem();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            } else {
                itemNode.item.setItemState(StackElementState.STACK_REMOVING);
            }
            itemNode = itemNode.prev;
        }
        return result;
    }

    @Override
    public void popToRoot() {
        StackNode<String, ActivityStackItem> itemNode = stackInfo.getLastNode();
        while (itemNode != null) {
            if (itemNode.prev == null) {
                break;
            }
            Activity activity = itemNode.item.getStackItem();
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            } else {
                itemNode.item.setItemState(StackElementState.STACK_REMOVING);
            }
            itemNode = itemNode.prev;
        }
    }

    @Override
    public ActivityStackItem peek() {
        return stackInfo.getLast();
    }

    @Override
    public boolean remove(String name) {
        ActivityStackItem stackItem = stackInfo.get(name);
        if (stackItem == null) {
            return false;
        }
        Activity activity = stackItem.getStackItem();
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        } else {
            stackItem.setItemState(StackElementState.STACK_REMOVING);
        }
        return true;
    }

    @Override
    public int size() {
        return stackInfo.getSize();
    }

    @Override
    public ActivityStackItem search(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        return stackInfo.get(name);
    }

    @Override
    public void clear(boolean keepTop) {
        StackNode<String, ActivityStackItem> node = stackInfo.getLastNode();
        if (node == null) {
            return;
        }
        if (keepTop) {
            node = node.prev;
        }
        while (node != null) {
            if (node.item.getStackItem() != null) {
                node.item.getStackItem().finish();
            } else {
                node.item.setItemState(StackElementState.STACK_REMOVING);
            }
            node = node.prev;
        }
    }

    public void moveToStackTop(Activity activity) {
        ActivityStackItem record = peek();
        if (record != null && record.getStackItem() == activity) {
            return;
        }
        String stackName = getNameInStack(activity);
        record = stackInfo.get(stackName);
        if (record == null) {
            record = new ActivityStackItem(stackName);
            stackInfo.put(record.getName(), record);
        } else {
            stackInfo.moveAllNext(stackName);
        }
        record.bindItem(activity);
        record.setItemState(StackElementState.STACK_TOP);
    }

    public String getNameInStack(Activity activity) {
        String stackName = activity.getIntent().getStringExtra(AppStackUtil.STACK_ITEM_NAME);
        if (stackName == null || stackName.length() == 0) {
            stackName = activity.getClass().getName();
        }
        return stackName;
    }

}