package com.zpf.frame;

import com.zpf.api.ICallback;
import com.zpf.api.ICustomWindow;
import com.zpf.api.IManager;
import com.zpf.api.LifecycleListener;
import com.zpf.api.OnDestroyListener;

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

    //绑定生命周期的弹窗
    void show(final ICustomWindow window);

    //关闭当前弹窗
    boolean dismiss();

    //绑定生命周期的网络请求控制器
    IManager<ICallback> getCallBackManager();

    void addLifecycleListener(LifecycleListener lifecycleListener);

    void removeLifecycleListener(LifecycleListener lifecycleListener);

    void addOnDestroyListener(OnDestroyListener listener);

    void removeOnDestroyListener(OnDestroyListener listener);

    void addResultCallBackListener(ResultCallBackListener callBackListener);

    void removeResultCallBackListener(ResultCallBackListener callBackListener);
}
