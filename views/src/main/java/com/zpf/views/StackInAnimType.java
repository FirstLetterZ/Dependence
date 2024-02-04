package com.zpf.views;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef(value = {StackInAnimType.IN_LEFT, StackInAnimType.IN_LEFT_OUT_RIGHT,
        StackInAnimType.IN_RIGHT, StackInAnimType.IN_RIGHT_OUT_LEFT, StackInAnimType.IN_BOTTOM,
        StackInAnimType.IN_ZOOM, StackInAnimType.IN_ZOOM_OUT_ZOOM, StackInAnimType.NONE,
})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StackInAnimType {
    int IN_LEFT = 1;
    int IN_LEFT_OUT_RIGHT = 2;
    int IN_RIGHT = 3;
    int IN_RIGHT_OUT_LEFT = 4;
    int IN_BOTTOM = 5;
    int IN_ZOOM = 6;
    int IN_ZOOM_OUT_ZOOM = 7;
    int NONE = 8;
}
