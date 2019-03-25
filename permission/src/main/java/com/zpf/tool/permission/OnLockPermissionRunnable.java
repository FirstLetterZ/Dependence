package com.zpf.tool.permission;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于缺少权限时执行
 * Created by ZPF on 2018/8/24.
 */
public abstract class OnLockPermissionRunnable implements Runnable {
    private final List<PermissionInfo> permissions = new ArrayList<>();

    public List<PermissionInfo> getPermissions() {
        return permissions;
    }

    @Override
    public void run() {
        onLock(permissions);
    }

    public abstract void onLock(List<PermissionInfo> list);
}
