package com.zpf.api;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Activity跳转路由
 * Created by ZPF on 2018/8/22.
 */
public interface IRouter {

    void startActivity(Context context, int targetId, @Nullable Intent intent);

    void startActivity(Context context, int targetId, @Nullable Intent intent, @Nullable Bundle options);

    //Activity,Service,Fragment都继承ComponentCallbacks
    void startActivityForResult(ComponentCallbacks componentCallbacks, int targetId,
                                @Nullable Intent intent, int requestCode);

    void startActivityForResult(ComponentCallbacks componentCallbacks, int targetId,
                                @Nullable Intent intent, int requestCode, @Nullable Bundle options);

}
