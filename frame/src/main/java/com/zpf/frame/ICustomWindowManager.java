package com.zpf.frame;

import com.zpf.api.ICustomWindow;
import com.zpf.api.OnAttachListener;

/**
 * @author Created by ZPF on 2021/4/2.
 */
public interface ICustomWindowManager {

    //绑定生命周期的弹窗
    void show(final ICustomWindow window);

    //关闭当前弹窗
    boolean close();

    void showLoading();

    void showLoading(Object msg);

    boolean hideLoading();

    void setLoadingListener(OnAttachListener listener);

}