package com.zpf.frame;

import com.zpf.api.OnAttachListener;

/**
 * @author Created by ZPF on 2021/4/2.
 */
public interface ILoadingManager {

    void showLoading();

    void showLoading(Object msg);

    boolean hideLoading();

    void setLoadingListener(OnAttachListener listener);

}