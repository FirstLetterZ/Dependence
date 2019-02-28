package com.zpf.api;

/**
 * 管理器
 * Created by ZPF on 2019/2/28.
 */
public interface IManager<T> {

    long bind(T t);

    void cancel(long id);

    void cancelAll();

}
