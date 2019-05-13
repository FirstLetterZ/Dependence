package com.zpf.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 完整的生命周期监听
 * Created by ZPF on 2019/2/28.
 */
public interface IFullLifecycle extends OnDestroyListener {
    void onCreate(@Nullable Bundle savedInstanceState);

    void onRestart();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(@NonNull Bundle outState);

    void onRestoreInstanceState(@NonNull Bundle savedInstanceState);

}
