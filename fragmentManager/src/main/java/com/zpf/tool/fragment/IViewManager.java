package com.zpf.tool.fragment;

/**
 * @author Created by ZPF on 2021/2/5.
 */
public interface IViewManager<K,V> {

    IViewManager<K,V> add(int parentId, K key);

    IViewManager<K,V> remove(K tagName);

    void clear(int parentId);

    void show(K key);

    void hide(K key);

    boolean commit();

    V getView(K key);
}
