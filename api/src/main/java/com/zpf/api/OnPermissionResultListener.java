package com.zpf.api;

import androidx.annotation.NonNull;


/**
 * Created by ZPF on 2019/5/13.
 */
public interface OnPermissionResultListener {

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

}
