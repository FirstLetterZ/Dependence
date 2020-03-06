package com.zpf.tool;

import android.support.annotation.NonNull;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * 来自 retrofit2.Utils
 */
public class GenericArrayTypeImpl implements GenericArrayType {
    private final Type componentType;

    public GenericArrayTypeImpl(Type componentType) {
        this.componentType = componentType;
    }

    @NonNull
    @Override public Type getGenericComponentType() {
        return componentType;
    }

    @Override public boolean equals(Object o) {
        return o instanceof GenericArrayType
                && TypeUtil.equals(this, (GenericArrayType) o);
    }

    @Override public int hashCode() {
        return componentType.hashCode();
    }

    @NonNull
    @Override public String toString() {
        return TypeUtil.typeToString(componentType) + "[]";
    }
}
