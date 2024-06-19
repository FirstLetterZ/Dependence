package com.zpf.file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompressErrorCode {
    int SUCCESS_NATIVE = 1;
    int SUCCESS_ANDROID = 2;
    int ERROR_READ_FILE = -11;
    int ERROR_CHECK_OPTION = -21;
    int ERROR_CREATE_FILE = -22;
    int ERROR_WHITE_FILE = -29;
    int ERROR_UNKNOWN = -99;
}
