package com.zpf.tool.event.api;

/**
 * 事件注册分发
 * Created by ZPF on 2019/7/18.
 */
public interface IEventManager {
    boolean register(IEventReceiver receiver);

    boolean unregister(IEventReceiver receiver);

    void post(IEvent event);

    void postInfallible(IEvent event);
}
