package com.zpf.api;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by ZPF on 2018/6/16.
 */
public interface IPackedLayout {

    @NonNull
    View getCurrentChild();

    boolean showChildByKey(@IntRange(from = 1, to = 16) int key);

    void addChildByKey(@IntRange(from = 1, to = 16) int key, View view);
}
