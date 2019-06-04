package com.zpf.api;

/**
 * 管理器
 * Created by ZPF on 2019/2/28.
 */
public interface IManager<T> extends OnDestroyListener{

    long bind(T t);

    boolean execute(long id);

    void remove(long id);

    void cancel(long id);

    void cancelAll();

    void reset();

}
