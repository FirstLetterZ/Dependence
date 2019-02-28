package com.zpf.api;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by ZPF on 2018/7/17.
 */
public interface OnProgressListener<T extends View> {
    /**
     * @param view 对应视图
     * @param total 总数据量
     * @param current 当前进度量
     */
    void onChanged(@Nullable T view, long total, long current);
}
