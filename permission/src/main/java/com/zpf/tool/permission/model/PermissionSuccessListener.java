package com.zpf.tool.permission.model;

import androidx.annotation.Nullable;

import com.zpf.tool.permission.PermissionManager;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;

import java.util.List;

abstract class PermissionSuccessListener implements IPermissionResultListener {
    @Override
    public void onPermissionChecked(boolean formResult, int requestCode, String[] requestPermissions, @Nullable List<String> missPermissions) {
        if (missPermissions == null || missPermissions.size() == 0) {
            onGranted();
        } else {
            PermissionManager.get().callDefaultCallBack(formResult, requestCode, requestPermissions, missPermissions);
        }
    }

    protected abstract void onGranted();
}
