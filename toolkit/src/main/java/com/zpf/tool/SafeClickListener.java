package com.zpf.tool;

import android.os.SystemClock;
import android.view.View;

/**
 * Created by ZPF on 2018/4/16.
 */
public abstract class SafeClickListener implements View.OnClickListener {
    public static long lastClick = 0;
    private long timeInterval = 200;

    public SafeClickListener(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    public void onClick(View v) {
        //防止快速点击
        long currentTime = SystemClock.elapsedRealtime();
        long dTime = currentTime - lastClick;
        if (dTime < 0 || dTime > timeInterval) {
            lastClick = currentTime;
            if (checkCondition(v)) {
                click(v);
            }
        }
    }

    protected boolean checkCondition(View v) {
        return true;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public abstract void click(View v);
}
