package com.zpf.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 视图常见生命周期监听
 * Created by ZPF on 2019/2/28.
 */
public interface IViewLifecycle extends OnDestroyListener {

    void onCreate(@Nullable Bundle savedInstanceState);

    void onVisibleChanged(boolean visibility);

    void onActivityChanged(boolean isActivity);

    void onSaveInstanceState(@NonNull Bundle outState);

    void onRestoreInstanceState(@NonNull Bundle savedInstanceState);

}
