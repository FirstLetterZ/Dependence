package com.zpf.tool;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class BundleSetUtil {
    public static boolean setData(Bundle bundle, String name, Object value, boolean saveStringOnFail) {
        if (bundle == null || name == null || value == null) {
            return false;
        }
        Class<?> dataClass = value.getClass();
        boolean saved = false;
        if (dataClass.isArray()) {
            saved = saveComponentArray(bundle, name, value);
        } else if (value instanceof ArrayList) {
            saved = saveArrayList(bundle, name, value);
        } else if (value instanceof SparseArray) {
            Class<?> genericType = getListClass(value.getClass());
            if (genericType != null && Parcelable.class.isAssignableFrom(genericType)) {
                bundle.putSparseParcelableArray(name, (SparseArray<? extends Parcelable>) value);
                saved = true;
            }
        } else if (value instanceof Bundle) {
            bundle.putAll(bundle);
            saved = true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && value instanceof PersistableBundle) {
            bundle.putAll(bundle);
            saved = true;
        } else {
            saved = saveData(bundle, name, value);
        }
        if (!saved && saveStringOnFail) {
            bundle.putString(name, value.toString());
            saved = true;
        }
        return saved;
    }

    private static boolean saveArrayList(@NonNull Bundle bundle, @NonNull String name, @NonNull Object value) {
        Class<?> genericType = getListClass(value.getClass());
        if (genericType == null) {
            return false;
        }
        if (String.class.isAssignableFrom(genericType)) {
            bundle.putStringArrayList(name, (ArrayList<String>) value);
        } else if (Integer.class.isAssignableFrom(genericType)) {
            bundle.putIntegerArrayList(name, (ArrayList<Integer>) value);
        } else if (CharSequence.class.isAssignableFrom(genericType)) {
            bundle.putCharSequenceArrayList(name, (ArrayList<CharSequence>) value);
        } else if (Parcelable.class.isAssignableFrom(genericType)) {
            bundle.putParcelableArrayList(name, (ArrayList<? extends Parcelable>) value);
        } else {
            return false;
        }
        return true;
    }

    private static boolean saveComponentArray(@NonNull Bundle bundle, @NonNull String name, @NonNull Object value) {
        if (value instanceof String[]) {
            bundle.putStringArray(name, (String[]) value);
        } else if (value instanceof int[]) {
            bundle.putIntArray(name, (int[]) value);
        } else if (value instanceof boolean[]) {
            bundle.putBooleanArray(name, (boolean[]) value);
        } else if (value instanceof char[]) {
            bundle.putCharArray(name, (char[]) value);
        } else if (value instanceof CharSequence[]) {
            bundle.putCharSequenceArray(name, (CharSequence[]) value);
        } else if (value instanceof byte[]) {
            bundle.putByteArray(name, (byte[]) value);
        } else if (value instanceof long[]) {
            bundle.putLongArray(name, (long[]) value);
        } else if (value instanceof float[]) {
            bundle.putFloatArray(name, (float[]) value);
        } else if (value instanceof double[]) {
            bundle.putDoubleArray(name, (double[]) value);
        } else if (value instanceof short[]) {
            bundle.putShortArray(name, (short[]) value);
        } else if (value instanceof Parcelable[]) {
            bundle.putParcelableArray(name, (Parcelable[]) value);
        } else {
            return false;
        }
        return true;
    }

    private static boolean saveData(@NonNull Bundle bundle, @NonNull String name, @NonNull Object value) {
        if (value instanceof String) {
            bundle.putString(name, (String) value);
        } else if (value instanceof Integer) {
            bundle.putInt(name, (int) value);
        } else if (value instanceof Boolean) {
            bundle.putBoolean(name, (boolean) value);
        } else if (value instanceof Float) {
            bundle.putFloat(name, (float) value);
        } else if (value instanceof Long) {
            bundle.putLong(name, (long) value);
        } else if (value instanceof Double) {
            bundle.putDouble(name, (double) value);
        } else if (value instanceof Short) {
            bundle.putShort(name, (short) value);
        } else if (value instanceof Byte) {
            bundle.putByte(name, (byte) value);
        } else if (value instanceof Character) {
            bundle.putChar(name, (char) value);
        } else if (value instanceof Parcelable) {
            bundle.putParcelable(name, (Parcelable) value);
        } else if (value instanceof Serializable) {
            bundle.putSerializable(name, (Serializable) value);
        }else {
            return false;
        }
        return true;
    }

    private static Class<?> getListClass(Class<?> dataClass) {
        Type type = dataClass.getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types.length != 1) {
            return null;
        }
        return (Class<?>) types[0];
    }

}
