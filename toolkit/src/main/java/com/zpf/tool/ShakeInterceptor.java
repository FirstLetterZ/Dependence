package com.zpf.tool;

/**
 * Created by ZPF on 2018/12/4.
 */
public class ShakeInterceptor {

    private long lastClick = 0;
    private long timeInterval = 200;

    public ShakeInterceptor() {
    }

    public ShakeInterceptor(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public boolean checkInterval() {
        if ((System.currentTimeMillis() - lastClick) > timeInterval) {
            lastClick = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
}
