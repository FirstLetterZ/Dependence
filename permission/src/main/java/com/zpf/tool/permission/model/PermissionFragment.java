package com.zpf.tool.permission.model;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.zpf.tool.permission.PermissionManager;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;

import java.util.ArrayList;
import java.util.List;

public class PermissionFragment extends Fragment {
    public static final String TAG = "PermissionFragment";
    private IPermissionResultListener cacheListener;

    public void callRequestPermissions(String[] p, int code, IPermissionResultListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cacheListener = listener;
            requestPermissions(p, code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        List<String> missPermissionList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (permissions.length > i) {
                    missPermissionList.add(permissions[i]);
                }
            }
        }
        IPermissionResultListener listener;
        if (cacheListener == null) {
            listener = PermissionManager.get().defCallBack;
        } else {
            listener = cacheListener;
        }
        cacheListener = null;
        if (listener != null) {
            listener.onPermissionChecked(true, requestCode, permissions, missPermissionList);
        }
    }
}
