package com.zpf.tool.compat.permission;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.zpf.tool.permission.interfaces.IPermissionChecker;

import java.lang.ref.WeakReference;

/**
 * 使用androidx.fragment.app.Fragment检查权限
 */
public class CompatPermissionChecker implements IPermissionChecker {
    private WeakReference<Fragment> mReference;

    @Override
    public boolean shouldHandleRequest(Object requester) {
        if (requester instanceof Fragment) {
            mReference = new WeakReference<>(((Fragment) requester));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String p) {
        Fragment fragment = mReference.get();
        if (fragment != null) {
            fragment.shouldShowRequestPermissionRationale(p);
        }
        return false;
    }

    @Override
    public void requestPermissions(String[] p, int code) {
        Fragment fragment = mReference.get();
        if (fragment != null) {
            fragment.requestPermissions(p, code);
        }
    }

    @Override
    public Context getContext() {
        Fragment fragment = mReference.get();
        if (fragment != null) {
            return fragment.getContext();
        }
        return null;
    }
}
