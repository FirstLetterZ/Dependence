package com.zpf.tool.permission;

import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.SparseArray;

import com.zpf.api.IPermissionResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZPF on 2018/8/22.
 */
public abstract class PermissionChecker<T> {
    public static final int REQ_PERMISSION_CODE = 10001;
    public static final String PERMISSION_RECORD = "app_permission_record_file";
    protected final SparseArray<IPermissionResult> permissionCallBack = new SparseArray<>();
    private IPermissionResult defHandler;

    public boolean checkPermissions(T target, String... permissions) {
        return checkPermissions(target, REQ_PERMISSION_CODE, null, permissions);
    }

    public boolean checkPermissions(T target, int requestCode, String... permissions) {
        return checkPermissions(target, requestCode, null, permissions);
    }

    public boolean checkPermissions(T target, int requestCode, IPermissionResult listener, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkEffective(target)) {
                return false;
            }
            List<String> missPermissionList = new ArrayList<>();
            SharedPreferences.Editor editor = null;
            SharedPreferences sp = getSharedPreferences(target);
            boolean showCustomRationale = false;
            for (String per : permissions) {
                if (!hasPermission(target, per)) {
                    if (sp != null && !sp.getBoolean(per, false)) {
                        if (editor == null) {
                            editor = sp.edit();
                        }
                        editor.putBoolean(per, true);
                    } else if (!shouldShowRequestPermissionRationale(target, per)) {
                        showCustomRationale = true;
                    }
                    missPermissionList.add(per);
                }
            }
            if (editor != null) {
                editor.commit();
            }
            if (showCustomRationale) {
                if (listener != null) {
                    listener.onPermissionChecked(false, requestCode, permissions, missPermissionList);
                }
                return false;
            } else if (missPermissionList.size() > 0) {
                if (listener != null) {
                    permissionCallBack.put(requestCode, listener);
                }
                requestPermissions(target, missPermissionList, requestCode);
                return false;
            } else {
                if (listener != null) {
                    listener.onPermissionChecked(false, requestCode, permissions, null);
                }
                return true;
            }
        } else {
            if (listener != null) {
                listener.onPermissionChecked(false, requestCode, permissions, null);
            }
            return true;
        }
    }

    public void requestPermissions(T target, List<String> missPermissionList, int code) {
        if (missPermissionList != null && missPermissionList.size() > 0) {
            int size = missPermissionList.size();
            realRequestPermissions(target, missPermissionList.toArray(new String[size]), code);
        }
    }

    //检查是否拥有权限
    public abstract boolean hasPermission(T target, String p);

    //检查传入对象是否为有效
    protected abstract boolean checkEffective(T target);

    //打开SharedPreferences获取权限是否第一次请求
    protected abstract SharedPreferences getSharedPreferences(T target);

    //是否可以使用系统弹窗请求权限
    protected abstract boolean shouldShowRequestPermissionRationale(T target, String p);

    //请求权限
    protected abstract void realRequestPermissions(T target, String[] p, int code);

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        final IPermissionResult resultListener = permissionCallBack.get(requestCode);
        permissionCallBack.clear();
        List<String> missPermissionList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (permissions.length > i) {
                    missPermissionList.add(permissions[i]);
                }
            }
        }
        if (resultListener != null) {
            resultListener.onPermissionChecked(true, requestCode, permissions, missPermissionList);
        } else if (defHandler != null) {
            defHandler.onPermissionChecked(true, requestCode, permissions, missPermissionList);
        }
    }

    public void setDefHandler(IPermissionResult defHandler) {
        this.defHandler = defHandler;
    }

    public void clearCallBack() {
        permissionCallBack.clear();
    }

    public static boolean checkWriteSetting(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.System.canWrite(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            }
        }
        return true;
    }

    public static boolean checkDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                Intent serviceIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                context.startActivity(serviceIntent);
                return false;
            }
        }
        return true;
    }

    public static boolean checkNoticeEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            return mNotificationManager != null && mNotificationManager.areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOps =
                    (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            try {
                Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE,
                        Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
                        == AppOpsManager.MODE_ALLOWED);
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
                    | InvocationTargetException | IllegalAccessException | RuntimeException e) {
                return true;
            }
        } else {
            return true;
        }
    }

}
