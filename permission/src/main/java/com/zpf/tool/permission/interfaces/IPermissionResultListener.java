package com.zpf.tool.permission.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * @author Created by ZPF on 2021/6/18.
 */
public interface IPermissionResultListener {
    void onPermissionChecked(boolean formResult, int requestCode, @NonNull String[] requestPermissions, @Nullable List<String> missPermissions);
}
