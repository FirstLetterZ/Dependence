package com.zpf.tool.compat.permission;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

/**
 * 使用android.support.v4.app.Fragment检查权限
 * Created by ZPF on 2018/8/22.
 */
public class CompatPermissionChecker extends PermissionChecker<Fragment> {

    @Override
    boolean checkEffective(Fragment target) {
        return target != null && target.getContext() != null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    boolean hasPermission(Fragment target, String p) {
        try {
            return target.getContext().checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    void requestPermissions(Fragment target, String[] p, int code) {
        target.requestPermissions(p, code);
    }
}
