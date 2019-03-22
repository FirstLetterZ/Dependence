package com.zpf.tool.compat.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 使用activity检查权限
 * Created by ZPF on 2018/8/22.
 */
@TargetApi(Build.VERSION_CODES.M)
public class ActivityPermissionChecker extends PermissionChecker<Activity> {

    @Override
    protected boolean checkEffective(Activity target) {
        return target != null;
    }

    @Override
    protected boolean hasPermission(Activity target, String p) {
        return target.checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void requestPermissions(Activity target, String[] p, int code) {
        target.requestPermissions(p, code);
    }

}
