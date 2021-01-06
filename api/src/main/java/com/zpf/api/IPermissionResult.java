package com.zpf.api;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * 权限申请结果回调
 * Created by ZPF on 2020/1/6.P
 */
public interface IPermissionResult {

    void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions,
                             @Nullable List<String> missPermissions);

}
