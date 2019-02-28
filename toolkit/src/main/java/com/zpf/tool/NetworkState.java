package com.zpf.tool;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZPF on 2018/6/13.
 */
@IntDef(value = {NetworkState.NETWORK_NONE, NetworkState.NETWORK_WIFI,
        NetworkState.NETWORK_2G, NetworkState.NETWORK_3G, NetworkState.NETWORK_4G,
        NetworkState.NETWORK_5G, NetworkState.NETWORK_UNKNOWN_MOBILE})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NetworkState {
    int NETWORK_NONE = 0;
    int NETWORK_WIFI = 1;
    int NETWORK_2G = 2;
    int NETWORK_3G = 3;
    int NETWORK_4G = 4;
    int NETWORK_5G = 5;
    int NETWORK_UNKNOWN_MOBILE = 6;
}
