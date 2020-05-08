package com.zpf.frame;

/**
 * Created by ZPF on 2019/3/1.
 */

public interface ILoadingManager {
    void showLoading();

    void showLoading(Object msg);

    boolean hideLoading();

    void addStateListener(ILoadingStateListener listener);

    void removeStateListener(ILoadingStateListener listener);

    Object getLoadingView();
}
