package com.zpf.api;

/**
 * 事件注册分发
 * Created by ZPF on 2019/7/18.
 */
public interface IEventManager {
    void register(String receiverName, IFunction1<IEvent<?>> receiver);

    void unregister(String receiverName);

    void post(String receiverName, IEvent<?> event);

    void postInfallible(String receiverName, IEvent<?> event);
}
