package com.zpf.tool.fragment;

/**
 * @author Created by ZPF on 2021/2/7.
 */
public interface IViewCreator<K, V> {
    V create(K key);
}
