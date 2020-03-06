package com.zpf.tool;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * 来自 retrofit2.Utils
 */
public class WildcardTypeImpl implements WildcardType {
    private final Type upperBound;
    @Nullable
    private final Type lowerBound;

    public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
        if (lowerBounds.length > 1) throw new IllegalArgumentException();
        if (upperBounds.length != 1) throw new IllegalArgumentException();

        if (lowerBounds.length == 1) {
            if (lowerBounds[0] == null) throw new NullPointerException();
            TypeUtil.checkNotPrimitive(lowerBounds[0]);
            if (upperBounds[0] != Object.class) throw new IllegalArgumentException();
            this.lowerBound = lowerBounds[0];
            this.upperBound = Object.class;
        } else {
            if (upperBounds[0] == null) throw new NullPointerException();
            TypeUtil.checkNotPrimitive(upperBounds[0]);
            this.lowerBound = null;
            this.upperBound = upperBounds[0];
        }
    }

    @NonNull
    @Override
    public Type[] getUpperBounds() {
        return new Type[]{upperBound};
    }

    @NonNull
    @Override
    public Type[] getLowerBounds() {
        return lowerBound != null ? new Type[]{lowerBound} : TypeUtil.EMPTY_TYPE_ARRAY;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof WildcardType && TypeUtil.equals(this, (WildcardType) other);
    }

    @Override
    public int hashCode() {
        // This equals Arrays.hashCode(getLowerBounds()) ^ Arrays.hashCode(getUpperBounds()).
        return (lowerBound != null ? 31 + lowerBound.hashCode() : 1) ^ (31 + upperBound.hashCode());
    }

    @NonNull
    @Override
    public String toString() {
        if (lowerBound != null) return "? super " + TypeUtil.typeToString(lowerBound);
        if (upperBound == Object.class) return "?";
        return "? extends " + TypeUtil.typeToString(upperBound);
    }
}
