package com.zpf.tool.stack;

/**
 * Created by ZPF on 2019/5/15.
 */

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef(value = {StackElementState.STACK_OUTSIDE, StackElementState.STACK_TOP,
        StackElementState.STACK_INSIDE, StackElementState.STACK_REMOVING
})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StackElementState {
    int STACK_OUTSIDE = 0;
    int STACK_TOP = 1;
    int STACK_INSIDE = 2;
    int STACK_REMOVING = 3;
}
