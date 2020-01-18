package com.zpf.api;

/**
 * Created by ZPF on 2018/7/17.
 */
public interface OnProgressListener {
    /**
     * @param total 总数据量
     * @param current 当前进度量
     */
    void onChanged( long total, long current);
}
