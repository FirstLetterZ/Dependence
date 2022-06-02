package com.zpf.api;

import android.os.Bundle;

/**
 * Created by ZPF on 2019/5/14.
 */
public interface OnViewStateChangedListener {
    void onParamChanged(Bundle newParams);

    void onVisibleChanged(boolean visible);
}