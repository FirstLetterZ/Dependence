package com.zpf.tool.fragment;

/**
 * @author Created by ZPF on 2021/2/5.
 */
public interface IViewManager<T> {

    IViewManager<T> add(int parentId, String tagName, T child);

    IViewManager<T> add(int parentId, String tagName, Class<T> childClass);

    IViewManager<T> remove(String tagName);

    IViewManager<T> remove(T child);

    void clear(int parentId);

    void show(String tagName);

    void show(T child);

    void hide(String tagName);

    void hide(T child);

    void commit();

    T get(String tagName);
}
