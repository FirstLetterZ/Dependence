package com.zpf.api;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by ZPF on 2018/6/13.
 */
public interface OnActivityResultListener {
    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}