package com.zpf.update;

/**
 * @author Created by ZPF on 2021/3/29.
 */
public interface IDownloadListener extends INetResultListener {
    void onChanged(String fileName, long total, long current);
}
