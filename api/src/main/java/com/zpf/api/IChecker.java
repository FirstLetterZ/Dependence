package com.zpf.api;

/**
 * 条件检查
 * Created by ZPF on 2019/2/28.
 */
public interface IChecker<T> {
    boolean check(T t);
}
