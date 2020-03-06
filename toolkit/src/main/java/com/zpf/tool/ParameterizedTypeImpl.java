package com.zpf.tool;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * 来自 retrofit2.Utils
 */
public class ParameterizedTypeImpl implements ParameterizedType {
    @Nullable
    private final Type ownerType;
    private final Type rawType;
    private final Type[] typeArguments;

    public ParameterizedTypeImpl(@Nullable Type ownerType, Type rawType, Type... typeArguments) {
        // Require an owner type if the raw type needs it.
        if (rawType instanceof Class<?>
                && (ownerType == null) != (((Class<?>) rawType).getEnclosingClass() == null)) {
            throw new IllegalArgumentException();
        }

        for (Type typeArgument : typeArguments) {
            Objects.requireNonNull(typeArgument, "typeArgument == null");
            TypeUtil.checkNotPrimitive(typeArgument);
        }

        this.ownerType = ownerType;
        this.rawType = rawType;
        this.typeArguments = typeArguments.clone();
    }

    @NonNull
    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments.clone();
    }

    @NonNull
    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public @Nullable
    Type getOwnerType() {
        return ownerType;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ParameterizedType && TypeUtil.equals(this, (ParameterizedType) other);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(typeArguments)
                ^ rawType.hashCode()
                ^ (ownerType != null ? ownerType.hashCode() : 0);
    }

    @NonNull
    @Override
    public String toString() {
        if (typeArguments.length == 0) return TypeUtil.typeToString(rawType);
        StringBuilder result = new StringBuilder(30 * (typeArguments.length + 1));
        result.append(TypeUtil.typeToString(rawType));
        result.append("<").append(TypeUtil.typeToString(typeArguments[0]));
        for (int i = 1; i < typeArguments.length; i++) {
            result.append(", ").append(TypeUtil.typeToString(typeArguments[i]));
        }
        return result.append(">").toString();
    }
}
