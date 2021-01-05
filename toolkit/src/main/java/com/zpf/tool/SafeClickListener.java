package com.zpf.tool;

import android.view.View;

/**
 * Created by ZPF on 2018/4/16.
 */
public abstract class SafeClickListener implements View.OnClickListener {
    private long lastClick = 0;
    private long timeInterval = 200;

    public SafeClickListener() {
    }

    public SafeClickListener(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    public void onClick(View v) {
        //防止快速点击
        if (checkInterval(System.currentTimeMillis() - lastClick)) {
            lastClick = System.currentTimeMillis();
            if (checkCondition(v)) {
                click(v);
            }
        }
    }

    private boolean checkInterval(long interval) {
        return interval > timeInterval;
    }

    protected boolean checkCondition(View v) {
        return true;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public abstract void click(View v);
}
