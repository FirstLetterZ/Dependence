package com.zpf.tool.compat.permission;

import android.content.pm.PackageManager;
import android.os.Build;

import androidx.fragment.app.Fragment;

import com.zpf.tool.permission.PermissionManager;
import com.zpf.tool.permission.interfaces.IPermissionResultListener;

import java.util.ArrayList;
import java.util.List;

public class CompatPermissionFragment extends Fragment {
    private IPermissionResultListener cacheListener;

    public void callRequestPermissions(String[] p, int code, IPermissionResultListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cacheListener = listener;
            requestPermissions(p, code);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
