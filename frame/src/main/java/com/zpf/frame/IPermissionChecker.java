package com.zpf.frame;

public interface IPermissionChecker {
    boolean checkPermissions(String... permissions);

    boolean checkPermissions(int requestCode, String... permissions);

    void checkPermissions(IPermissionResult callback, String... permissions);

    void checkPermissions(IPermissionResult callback, int requestCode, String... permissions);
}
