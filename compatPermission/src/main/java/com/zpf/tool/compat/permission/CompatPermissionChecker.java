package com.zpf.tool.compat.permission;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.fragment.app.Fragment;

import com.zpf.tool.permission.PermissionChecker;

/**
 * 使用android.support.v4.app.Fragment检查权限
 * Created by ZPF on 2018/8/22.
 */
@TargetApi(Build.VERSION_CODES.M)
public class CompatPermissionChecker extends PermissionChecker<Fragment> {

    @Override
    protected boolean checkEffective(Fragment target) {
        return target != null && target.getContext() != null;
    }

    @Override
    protected boolean shouldShowRequestPermissionRationale(Fragment target, String p) {
        return target.shouldShowRequestPermissionRationale(p);
    }

    @Override
    protected void realRequestPermissions(Fragment target, String[] p, int code) {
        target.requestPermissions(p, code);
    }

    @Override
    public boolean hasPermission(Fragment target, String p) {
        try {
            return target.getContext().checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }
}
