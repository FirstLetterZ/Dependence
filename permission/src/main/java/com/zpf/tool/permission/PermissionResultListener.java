package com.zpf.tool.permission;

import java.util.List;

public interface PermissionResultListener {
    void onPermissionCheck(boolean formResult, List<PermissionInfo> missPermissionList);
}
