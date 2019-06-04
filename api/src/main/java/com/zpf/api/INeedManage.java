package com.zpf.api;

/**
 * 需要绑定管理器
 * Created by ZPF on 2019/2/28.
 */
public interface INeedManage<T> {
    T toBind(IManager<T> manager);

    boolean unBind(long bindId);
}
