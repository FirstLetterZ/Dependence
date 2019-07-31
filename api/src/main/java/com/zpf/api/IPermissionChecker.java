package com.zpf.api;

/**
 * 权限申请
 * Created by ZPF on 2018/8/22.
 */
public interface IPermissionChecker {
    boolean checkPermissions(String... permissions);

    boolean checkPermissions(int requestCode, String... permissions);

    void checkPermissions(Runnable onPermission, Runnable onLock, String... permissions);

    void checkPermissions(Runnable onPermission, Runnable onLock, int requestCode, String... permissions);

}
