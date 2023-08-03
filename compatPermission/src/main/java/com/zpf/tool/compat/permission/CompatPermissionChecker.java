package com.zpf.tool.compat.permission;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.zpf.tool.permission.interfaces.IPermissionChecker;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;
import com.zpf.tool.permission.model.PermissionFragment;

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
    public void requestPermissions(String[] p, int code, IPermissionResultListener listener) {
        Fragment fragment = mReference.get();
        if (fragment == null) {
            return;
        }
        FragmentManager manager = null;
        try {
            manager = fragment.getChildFragmentManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (manager == null) {
            return;
        }
        Fragment cache = manager.findFragmentByTag(PermissionFragment.TAG);
        CompatPermissionFragment permissionFragment;
        if (cache instanceof CompatPermissionFragment) {
            permissionFragment = (CompatPermissionFragment) cache;
        } else {
            permissionFragment = new CompatPermissionFragment();
            manager.beginTransaction().add(permissionFragment, PermissionFragment.TAG).commitAllowingStateLoss();
            manager.executePendingTransactions();
        }
        permissionFragment.callRequestPermissions(p, code, listener);
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
