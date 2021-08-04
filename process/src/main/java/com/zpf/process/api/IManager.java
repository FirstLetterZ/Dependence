package com.zpf.process.api;

import android.os.Bundle;

/**
 * @author Created by ZPF on 2021/4/8.
 */
public interface IManager {
    IManager addEventHandler(IEventHandler handler);

    IManager removeEventHandler(IEventHandler handler);

    IManager setEventHandler(String event, IEventHandler handler);

    IEventHandler findEventHandler(IPredicate<IEventHandler> predicate);

    IManager clearEventHandler();

    void dispatchEvent(String event, Bundle params, IEventCallback eventCallback);

}
