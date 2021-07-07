package com.zpf.tool.permission.model;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.zpf.tool.permission.interfaces.IPermissionChecker;

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
    public boolean shouldShowRequestPermissionRationale(String p) {
        Activity activity = mReference.get();
        if (activity != null) {
            activity.shouldShowRequestPermissionRationale(p);
        }
        return false;
    }

    @Override
    public void requestPermissions(String[] p, int code) {
        Activity activity = mReference.get();
        if (activity != null) {
            activity.requestPermissions(p, code);
        }
    }

    @Override
    public Context getContext() {
        return mReference.get();
    }
}
