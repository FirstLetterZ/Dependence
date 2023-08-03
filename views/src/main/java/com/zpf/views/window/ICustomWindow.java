package com.zpf.views.window;

/**
 * Created by ZPF on 2019/2/27.
 */
public interface ICustomWindow {
    void show();

    void dismiss();

    boolean isShowing();

    boolean isCreated();

    ICustomWindow setManager(ICustomWindowManager manager);
}
