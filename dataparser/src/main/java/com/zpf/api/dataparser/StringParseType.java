package com.zpf.api.dataparser;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZPF on 2018/11/23.
 */
@IntDef(value = {
        StringParseType.TYPE_UNKNOWN, StringParseType.TYPE_NULL, StringParseType.TYPE_BOOLEAN,
        StringParseType.TYPE_NUMBER, StringParseType.TYPE_STRING, StringParseType.TYPE_JSON_OBJECT,
        StringParseType.TYPE_JSON_ARRAY
})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringParseType {
    int TYPE_UNKNOWN = 0;
    int TYPE_NULL = 1;
    int TYPE_BOOLEAN = 2;
    int TYPE_NUMBER = 3;
    int TYPE_STRING = 4;
    int TYPE_JSON_OBJECT = 5;
    int TYPE_JSON_ARRAY = 6;
}
