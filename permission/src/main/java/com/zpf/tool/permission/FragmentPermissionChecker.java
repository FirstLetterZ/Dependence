package com.zpf.tool.permission;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 使用android.app.Fragment检查权限
 * Created by ZPF on 2018/8/22.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FragmentPermissionChecker extends PermissionChecker<Fragment> {

    @Override
    protected boolean checkEffective(Fragment target) {
        return target != null && target.getActivity() != null;
    }

    @Override
    public boolean hasPermission(Fragment target, String p) {
        try {
            return target.getContext().checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected boolean shouldShowRequestPermissionRationale(Fragment target, String p) {
        return target.shouldShowRequestPermissionRationale(p);
    }

    @Override
    public void realRequestPermissions(Fragment target, String[] p, int code) {
        target.requestPermissions(p, code);
    }
}
