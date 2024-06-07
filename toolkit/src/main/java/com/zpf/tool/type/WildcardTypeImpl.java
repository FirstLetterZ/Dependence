package com.zpf.tool.type;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * 来自 com.google.gson.internal;
 * The WildcardType interface supports multiple upper bounds and multiple
 * lower bounds. We only support what the target Java version supports - at most one
 * bound, see also https://bugs.openjdk.java.net/browse/JDK-8250660. If a lower bound
 * is set, the upper bound must be Object.class.
 */
public class WildcardTypeImpl implements WildcardType, Serializable {
    private final Type upperBound;
    private final Type lowerBound;

    public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
        TypeTokenUtil.checkArgument(lowerBounds.length <= 1);
        TypeTokenUtil.checkArgument(upperBounds.length == 1);
        if (lowerBounds.length == 1) {
            TypeTokenUtil.checkNotNull(lowerBounds[0]);
            TypeTokenUtil.checkNotPrimitive(lowerBounds[0]);
            TypeTokenUtil.checkArgument(upperBounds[0] == Object.class);
            this.lowerBound = TypeTokenUtil.canonicalize(lowerBounds[0]);
            this.upperBound = Object.class;

        } else {
            TypeTokenUtil.checkNotNull(upperBounds[0]);
            TypeTokenUtil.checkNotPrimitive(upperBounds[0]);
            this.lowerBound = null;
            this.upperBound = TypeTokenUtil.canonicalize(upperBounds[0]);
        }
    }

    public Type[] getUpperBounds() {
        return new Type[] { upperBound };
    }

    public Type[] getLowerBounds() {
        return lowerBound != null ? new Type[] { lowerBound } : TypeTokenUtil.EMPTY_TYPE_ARRAY;
    }

    @Override public boolean equals(Object other) {
        return other instanceof WildcardType
                && TypeTokenUtil.equals(this, (WildcardType) other);
    }

    @Override public int hashCode() {
        // this equals Arrays.hashCode(getLowerBounds()) ^ Arrays.hashCode(getUpperBounds());
        return (lowerBound != null ? 31 + lowerBound.hashCode() : 1)
                ^ (31 + upperBound.hashCode());
    }

    @Override public String toString() {
        if (lowerBound != null) {
            return "? super " + TypeTokenUtil.typeToString(lowerBound);
        } else if (upperBound == Object.class) {
            return "?";
        } else {
            return "? extends " + TypeTokenUtil.typeToString(upperBound);
        }
    }
}