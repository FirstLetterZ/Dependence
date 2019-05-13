package com.zpf.frame;

import android.os.Bundle;

/**
 * Created by ZPF on 2019/5/13.
 */

public interface INavigator<T> {
    void navigate(T target);

    void navigate(T target, Bundle params);

    void navigate(T target, Bundle params, int requestCode);
}
