package com.zpf.tool.permission.model;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;

import com.zpf.tool.permission.interfaces.IPermissionChecker;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;

import java.lang.ref.WeakReference;

/**
 * 使用android.app.Fragment检查权限
 * Created by ZPF on 2018/8/22.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FragmentPermissionChecker implements IPermissionChecker {
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
        FragmentManager manager = fragment.getChildFragmentManager();
        if (manager == null) {
            return;
        }
        Fragment cache = manager.findFragmentByTag(PermissionFragment.TAG);
        PermissionFragment permissionFragment;
        if (cache instanceof PermissionFragment) {
            permissionFragment = (PermissionFragment) cache;
        } else {
            permissionFragment = new PermissionFragment();
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
