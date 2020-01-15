package com.zpf.tool;

import android.view.View;

/**
 * Created by ZPF
 */
public abstract class DoubleClickListener implements View.OnClickListener {
    private long lastClick = 0;
    private long timeInterval = 500;

    public DoubleClickListener() {
    }

    public DoubleClickListener(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    public void onClick(View v) {
        if (checkInterval(System.currentTimeMillis() - lastClick)) {
            click(v);
        } else {
            onFirstClick(v);
        }
        lastClick = System.currentTimeMillis();
    }

    private boolean checkInterval(long interval) {
        return interval < timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public abstract void click(View v);

    protected void onFirstClick(View v) {

    }
}
