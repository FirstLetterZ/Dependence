package com.zpf.frame;

import androidx.annotation.Nullable;

import java.util.List;

public interface IPermissionResult {
    void onPermissionChecked(int requestCode, String[] permissions, @Nullable List<String> missPermissions);
}
