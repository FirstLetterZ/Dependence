package com.zpf.api;

/**
 * 可取消的
 * Created by ZPF on 2019/5/30.
 */
public interface ICancelable {
    void cancel();

    boolean isCancelled();
}
