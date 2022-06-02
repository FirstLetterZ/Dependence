package com.zpf.api;

import androidx.annotation.Nullable;

/**
 * Created by ZPF on 2018/7/17.
 */
public interface OnProgressListener {
    /**
     * @param target 对应对象
     * @param total 总数据量
     * @param current 当前进度量
     */
    void onProgress(long total, long current, @Nullable Object target);
}
