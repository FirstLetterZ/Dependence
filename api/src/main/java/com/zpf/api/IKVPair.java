package com.zpf.api;

/**
 * 键值对
 * Created by ZPF on 2019/2/28.
 */
public interface IKVPair<K, V> {
    K getKey();

    V getValue();
}
