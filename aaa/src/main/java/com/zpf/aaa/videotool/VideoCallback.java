package com.zpf.aaa.videotool;

/**
 * Created by huzhuoren on 2021/4/30
 * Describe 合成 or 裁剪 回调
 */
public interface VideoCallback {
    void onSuccessful(String path);

    void onFailed(Exception e);
}
