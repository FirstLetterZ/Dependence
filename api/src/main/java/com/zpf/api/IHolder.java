package com.zpf.api;

/**
 * @author Created by ZPF on 2021/2/25.
 */
public interface IHolder<T> {

    Object getRoot();

    T findById(int id);

    T findByTag(String tag);
}
