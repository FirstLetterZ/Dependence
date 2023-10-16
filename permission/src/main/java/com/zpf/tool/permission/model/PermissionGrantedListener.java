package com.zpf.tool.permission.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zpf.tool.permission.PermissionManager;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;

import java.util.List;

public class PermissionGrantedListener implements IPermissionResultListener {
    private final Runnable onGranted;
    public PermissionGrantedListener(@NonNull Runnable runnable) {
        onGranted = runnable;
    }
    @Override
    public void onPermissionChecked(boolean formResult, int requestCode,  @NonNull String[] requestPermissions, @Nullable List<String> missPermissions) {
        if (missPermissions == null || missPermissions.size() == 0) {
            onGranted.run();
        } else {
            PermissionManager.get().callDefaultCallBack(formResult, requestCode, requestPermissions, missPermissions);
        }
    }
}