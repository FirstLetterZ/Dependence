package com.zpf.api;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by ZPF on 2018/7/17.
 */
public interface OnObjectProgressListener<T> {
    /**
     * @param obj 对应对象
     * @param total 总数据量
     * @param current 当前进度量
     */
    void onChanged(@Nullable T obj, long total, long current);
}
