package com.zpf.tool.permission.model;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;

import com.zpf.tool.permission.interfaces.IPermissionChecker;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;

import java.lang.ref.WeakReference;

/**
 * 使用activity检查权限
 * Created by ZPF on 2018/8/22.
 */
@TargetApi(Build.VERSION_CODES.M)
public class ActivityPermissionChecker implements IPermissionChecker {
    private WeakReference<Activity> mReference;

    @Override
    public boolean shouldHandleRequest(Object requester) {
        if (requester instanceof Activity) {
            mReference = new WeakReference<>(((Activity) requester));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void requestPermissions(String[] p, int code, IPermissionResultListener listener) {
        Activity activity = mReference.get();
        if (activity == null) {
            return;
        }
        FragmentManager manager = activity.getFragmentManager();
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
        return mReference.get();
    }
}
