package com.zpf.tool.compat.permission;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
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

    protected SharedPreferences getSharedPreferences(Fragment target) {
        return target != null && target.getContext() != null ? target.getContext().getSharedPreferences("app_permission_record_file", 0) : null;
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
        final Context context = target.getContext();
        if (context != null) {
            return context.checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
}
