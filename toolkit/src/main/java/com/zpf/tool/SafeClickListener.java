package com.zpf.tool;

import android.view.View;

/**
 * Created by ZPF on 2018/4/16.
 */
public abstract class SafeClickListener<T> implements View.OnClickListener {
    private long lastClick = 0;
    private long timeInterval = 200;
    private T condition;

    public SafeClickListener() {
    }

    public SafeClickListener(T condition) {
        this.condition = condition;
    }

    public SafeClickListener(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public SafeClickListener(long timeInterval, T condition) {
        this.timeInterval = timeInterval;
        this.condition = condition;
    }

    @Override
    public void onClick(View v) {
        //防止快速点击
        if (checkInterval(System.currentTimeMillis() - lastClick)) {
            lastClick = System.currentTimeMillis();
            if (checkCondition(v, condition)) {
                click(v);
            }
        }
    }

    protected boolean checkInterval(long interval) {
        return interval > timeInterval;
    }

    protected boolean checkCondition(View v, Object object) {
        return true;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }

    public abstract void click(View v);
}
