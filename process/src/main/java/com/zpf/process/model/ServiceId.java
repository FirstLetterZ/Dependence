package com.zpf.process.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ServiceId {
    int value();

    int FIRST_SERVICE = 1;
    int SECOND_SERVICE = 2;
    int THIRD_SERVICE = 3;
    int FOURTH_SERVICE = 4;
    int FIFTH_SERVICE = 5;
}