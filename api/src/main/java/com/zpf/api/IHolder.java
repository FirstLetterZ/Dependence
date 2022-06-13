package com.zpf.api;

import androidx.annotation.Nullable;

/**
 * @author Created by ZPF on 2021/2/25.
 */
public interface IHolder<T> {

    void onBindData(@Nullable Object data, int position);

    void onReceiveListener(@Nullable Object listener, int type);

    Object getRoot();

    T findById(int id);

    T findByTag(String tag);
}
