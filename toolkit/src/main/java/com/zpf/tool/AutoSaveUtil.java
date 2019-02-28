package com.zpf.tool;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by ZPF on 2019/1/12.
 */
public class AutoSaveUtil {
    private AutoSaveUtil() {
    }

    public static void save(Object object, Bundle savedInstanceState) {
        if (object == null || savedInstanceState == null) {
            return;
        }
        Object value;
        Class<?> fieldClass;
        Class<?> genericType;
        boolean saved;
        for (Field field : object.getClass().getDeclaredFields()) {
            AutoSave note = field.getAnnotation(AutoSave.class);
            if (note != null) {
                fieldClass = field.getType();
                field.setAccessible(true);
                try {
                    value = field.get(object);
                } catch (IllegalAccessException e) {
                    value = null;
                }
                if (value != null) {
                    try {
                        if (fieldClass.isArray()) {
                            saveComponentArray(savedInstanceState, field.getName(), value);
                        } else {
                            genericType = getComponentClass(field);
                            if (genericType == null) {
                                saved = saveData(savedInstanceState, field.getName(), value);
                            } else {
                                if (value instanceof ArrayList) {
                                    saved = true;
                                    if (String.class.isAssignableFrom(genericType)) {
                                        savedInstanceState.putStringArrayList(field.getName(),
                                                (ArrayList<String>) value);
                                    } else if (Integer.class.isAssignableFrom(genericType)) {
                                        savedInstanceState.putIntegerArrayList(field.getName(),
                                                (ArrayList<Integer>) value);
                                    } else if (CharSequence.class.isAssignableFrom(genericType)) {
                                        savedInstanceState.putCharSequenceArrayList(field.getName(),
                                                (ArrayList<CharSequence>) value);
                                    } else if (Parcelable.class.isAssignableFrom(genericType)) {
                                        savedInstanceState.putParcelableArrayList(field.getName(),
                                                (ArrayList<? extends Parcelable>) value);
                                    } else {
                                        //TODO
                                        saved = false;
                                    }
                                } else if (value instanceof SparseArray) {
                                    if (Parcelable.class.isAssignableFrom(genericType)) {
                                        savedInstanceState.putSparseParcelableArray(field.getName(),
                                                (SparseArray<? extends Parcelable>) value);
                                        saved = true;
                                    } else {
                                        saved = false;
                                    }
                                } else if (value instanceof Map) {
                                    saved = false;
                                    //TODO
                                } else if (value instanceof Collection) {
                                    saved = false;
                                    //TODO
                                } else {
                                    saved = false;
                                    //TODO
                                }
                            }
                            if (!saved && Serializable.class.isAssignableFrom(field.getType()) && value instanceof Serializable) {
                                savedInstanceState.putSerializable(field.getName(), (Serializable) value);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        field.setAccessible(false);
                    }
                }
            }
        }
    }

    public static void restore(Object object, Bundle savedInstanceState) {
        if (object == null || savedInstanceState == null) {
            return;
        }
        Class<?> fieldClass;
        if (savedInstanceState.size() > 0) {
            for (Field field : object.getClass().getDeclaredFields()) {
                AutoSave note = field.getAnnotation(AutoSave.class);
                if (note != null) {
                    field.setAccessible(true);
                    fieldClass = field.getType();
                    try {
                        if (fieldClass.isArray()) {
                            Object value = savedInstanceState.get(field.getName());
                            if (value != null) {
                                if (fieldClass.isInstance(value)) {
                                    field.set(object, value);
                                } else {
                                    int len = Array.getLength(value);
                                    Object realValue = Array.newInstance(fieldClass.getComponentType(), len);
                                    for (int i = 0; i < len; i++) {
                                        Array.set(realValue, i, Array.get(value, i));
                                    }
                                    field.set(object, realValue);
                                }
                            } else {
                                field.set(object, null);
                            }
                        } else if (int.class.isAssignableFrom(fieldClass)) {
                            field.set(object, savedInstanceState.getInt(field.getName()));
                        } else if (float.class.isAssignableFrom(fieldClass)) {
                            field.set(object, savedInstanceState.getFloat(field.getName()));
                        } else if (double.class.isAssignableFrom(fieldClass)) {
                            field.set(object, savedInstanceState.getDouble(field.getName()));
                        } else if (long.class.isAssignableFrom(fieldClass)) {
                            field.set(object, savedInstanceState.getLong(field.getName()));
                        } else if (short.class.isAssignableFrom(fieldClass)) {
                            field.set(object, savedInstanceState.getShort(field.getName()));
                        } else if (byte.class.isAssignableFrom(fieldClass)) {
                            field.set(object, savedInstanceState.getByte(field.getName()));
                        } else if (char.class.isAssignableFrom(fieldClass)) {
                            field.set(object, savedInstanceState.getChar(field.getName()));
                        } else if (boolean.class.isAssignableFrom(fieldClass)) {
                            field.set(object, savedInstanceState.getBoolean(field.getName()));
                        } else {
                            field.set(object, savedInstanceState.get(field.getName()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        field.setAccessible(false);
                    }
                }
            }
        }
    }

    private static void saveComponentArray(@NonNull Bundle savedInstanceState, @NonNull String name, @NonNull Object value) {
        if (value instanceof String[]) {
            savedInstanceState.putStringArray(name, (String[]) value);
        } else if (value instanceof int[]) {
            savedInstanceState.putIntArray(name, (int[]) value);
        } else if (value instanceof boolean[]) {
            savedInstanceState.putBooleanArray(name, (boolean[]) value);
        } else if (value instanceof char[]) {
            savedInstanceState.putCharArray(name, (char[]) value);
        } else if (value instanceof CharSequence[]) {
            savedInstanceState.putCharSequenceArray(name, (CharSequence[]) value);
        } else if (value instanceof byte[]) {
            savedInstanceState.putByteArray(name, (byte[]) value);
        } else if (value instanceof long[]) {
            savedInstanceState.putLongArray(name, (long[]) value);
        } else if (value instanceof float[]) {
            savedInstanceState.putFloatArray(name, (float[]) value);
        } else if (value instanceof double[]) {
            savedInstanceState.putDoubleArray(name, (double[]) value);
        } else if (value instanceof short[]) {
            savedInstanceState.putShortArray(name, (short[]) value);
        } else if (value instanceof Parcelable[]) {
            savedInstanceState.putParcelableArray(name, (Parcelable[]) value);
        } else {
            //TODO
        }
    }

    private static boolean saveData(@NonNull Bundle savedInstanceState, @NonNull String name, @NonNull Object value) {
        if (value instanceof String) {
            savedInstanceState.putString(name, (String) value);
        } else if (value instanceof Integer) {
            savedInstanceState.putInt(name, (int) value);
        } else if (value instanceof Boolean) {
            savedInstanceState.putBoolean(name, (boolean) value);
        } else if (value instanceof Float) {
            savedInstanceState.putFloat(name, (float) value);
        } else if (value instanceof Long) {
            savedInstanceState.putLong(name, (long) value);
        } else if (value instanceof Double) {
            savedInstanceState.putDouble(name, (double) value);
        } else if (value instanceof Short) {
            savedInstanceState.putShort(name, (short) value);
        } else if (value instanceof Byte) {
            savedInstanceState.putByte(name, (byte) value);
        } else if (value instanceof Character) {
            savedInstanceState.putChar(name, (char) value);
        } else if (value instanceof Parcelable) {
            savedInstanceState.putParcelable(name, (Parcelable) value);
        } else {
            return false;
//            if (value instanceof SparseIntArray) {
//                //TODO
//            } else if (value instanceof SparseLongArray) {
//                //TODO
//            } else if (value instanceof SparseBooleanArray) {
//                //TODO
//            }
        }
        return true;
    }

    private static Class<?> getComponentClass(Field field) {
        Type type = field.getGenericType();
        if (type == null || !(type instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types == null || types.length == 0) {
            return null;
        }
        return (Class<?>) types[0];
    }

}
