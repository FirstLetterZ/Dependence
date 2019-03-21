package com.zpf.frame;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by ZPF on 2018/6/13.
 */

public interface ResultCallBackListener {

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    void onNewIntent(@NonNull Intent intent);

    void onVisibleChanged(boolean visibility);

    boolean onInterceptBackPress();
}
