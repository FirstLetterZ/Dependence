package com.zpf.tool.fingerprint;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZPF on 2019/2/13.
 */
@IntDef(value = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FingerprintAuthState {
    int AUTH_INITING = 0;
    int AUTH_INIT_SUCCESS = 1;
    int AUTH_INIT_FAIL = 2;
    int AUTH_MANAGER_MISSING = 3;
    int AUTH_NO_FINGERPRINTS = 4;
    int AUTH_NOT_SUPPORT = 5;
    int AUTH_START = 6;
    int AUTH_FAIL = 7;
    int AUTH_OUT_RETRY = 8;
    int AUTH_ERROR = 9;
    int AUTH_SUCCESS = 10;
    int AUTH_CANCEL = 11;
}
