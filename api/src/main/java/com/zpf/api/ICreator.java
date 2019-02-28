package com.zpf.api;

/**
 * 构造器
 * Created by ZPF on 2019/2/28.
 */
public interface ICreator<T> {
    T create(int id, Object... params);
}
