package com.zpf.tool.config;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by ZPF on 2018/6/13.
 */
public class MainHandler extends Handler {

    private MainHandler(Looper looper) {
        super(looper);
    }
    private static volatile MainHandler mainHandler;

    public static MainHandler get() {
        if (mainHandler == null) {
            synchronized (MainHandler.class) {
                if (mainHandler == null) {
                    mainHandler = new MainHandler(Looper.getMainLooper());
                }
            }
        }
        return mainHandler;
    }

}
