package com.zpf.frame;

import androidx.annotation.Nullable;

import com.zpf.api.ICancelable;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;

import java.lang.reflect.Type;

/**
 * 常用的有生命周的监听器控制
 * Created by ZPF on 2018/6/28.
 */
public interface ILifecycleMonitor {
    //获取当前状态
    int getState();

    //已创建到销毁之间的状态
    boolean isLiving();

    //可交互的状态
    boolean isActive();

    //用户可见的状态
    boolean isVisible();

    //绑定生命周期的弹窗
    void show(final ICustomWindow window);

    //关闭当前弹窗
    boolean close();

    //绑定生命周期的请求控制器
    IManager<ICancelable> getCancelableManager();

    boolean addListener(Object listener, @Nullable Type listenerClass);

    boolean removeListener(Object listener, @Nullable Type listenerClass);
}
