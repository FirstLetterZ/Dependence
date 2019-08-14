package com.zpf.frame;

import android.os.Bundle;

/**
 * Created by ZPF on 2019/5/14.
 */

public interface IViewStateListener {
    void onParamChanged(Bundle newParams);

    void onVisibleChanged(boolean visible);

    void onActiviityChanged(boolean activity);

}