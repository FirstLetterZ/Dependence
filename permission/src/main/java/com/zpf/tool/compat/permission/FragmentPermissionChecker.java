package com.zpf.tool.compat.permission;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 使用android.app.Fragment检查权限
 * Created by ZPF on 2018/8/22.
 */
public class FragmentPermissionChecker extends PermissionChecker<Fragment> {
    @Override
    protected boolean checkEffective(Fragment target) {
        return target != null && target.getActivity() != null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected boolean hasPermission(Fragment target, String p) {
        try {
            return target.getContext().checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void requestPermissions(Fragment target, String[] p, int code) {
        target.requestPermissions(p, code);
    }
}
