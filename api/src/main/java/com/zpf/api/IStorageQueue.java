package com.zpf.api;

/**
 * Created by ZPF on 2019/1/24.
 */

public interface IStorageQueue<T> {

    IStorageQueue<T> add(T name, Object value);

    boolean commit();
}
