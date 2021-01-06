package com.zpf.tool.permission;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * 权限结果
 * Created by ZPF on 2020/1/5.
 */

public interface IPermissionListener {
    void onPermissionChecked(boolean formResult, @Nullable List<PermissionInfo> missPermissions);
}
