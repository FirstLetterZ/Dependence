package com.zpf.api;

/**
 * 对象初始化
 * Created by ZPF on 2019/7/18.
 */
public interface Initializer<T> {
    void onInit(T t);
}
