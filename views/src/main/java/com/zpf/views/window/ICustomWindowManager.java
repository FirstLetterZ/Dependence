package com.zpf.views.window;

/**
 * Created by ZPF on 2022/5/30.
 */
public interface ICustomWindowManager {
    boolean shouldShowImmediately(ICustomWindow window);

    void onShow(ICustomWindow window);

    void onClose(ICustomWindow window);

    boolean close();

    void release();

    void reset();
}