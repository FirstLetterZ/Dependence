package com.zpf.tool;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtil {

    public static boolean setValue(@Nullable Object source, @NonNull Class<?> target,
                                   @NonNull String name, @Nullable Object value) {
        Field field = null;
        int modify = 0;
        Field modifiersField = null;
        boolean removeFinal = false;
        boolean result = false;
        try {
            field = target.getDeclaredField(name);
            modify = field.getModifiers();
            //final修饰的基本类型不可修改
            if (field.getType().isPrimitive() && Modifier.isFinal(modify)) {
                return result;
            }
            //获取访问权限
            if (!Modifier.isPublic(modify) || Modifier.isFinal(modify)) {
                field.setAccessible(true);
            }
            //static final同时修饰
            removeFinal = Modifier.isStatic(modify) && Modifier.isFinal(modify);
            if (removeFinal) {
                try {
                    modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(field, modify & ~Modifier.FINAL);
                } catch (NoSuchFieldException e) {
                    field.setAccessible(true);
                }
            }
            //按照类型调用设置方法
            Class<?> fieldType = field.getType();
            if (fieldType.isPrimitive()) {
                if (int.class.isAssignableFrom(fieldType)) {
                    if (value instanceof Number) {
                        field.setInt(source, ((Number) value).intValue());
                        result = true;
                    }
                } else if (float.class.isAssignableFrom(fieldType)) {
                    if (value instanceof Number) {
                        field.setFloat(source, ((Number) value).floatValue());
                        result = true;
                    }
                } else if (double.class.isAssignableFrom(fieldType)) {
                    if (value instanceof Number) {
                        field.setDouble(source, ((Number) value).doubleValue());
                        result = true;
                    }
                } else if (long.class.isAssignableFrom(fieldType)) {
                    if (value instanceof Number) {
                        field.setLong(source, ((Number) value).longValue());
                    }
                } else if (short.class.isAssignableFrom(fieldType)) {
                    if (value instanceof Number) {
                        field.setShort(source, ((Number) value).shortValue());
                        result = true;
                    }
                } else if (byte.class.isAssignableFrom(fieldType)) {
                    if (value != null && byte.class.isAssignableFrom(value.getClass())) {
                        field.setByte(source, (byte) value);
                        result = true;
                    }
                } else if (char.class.isAssignableFrom(fieldType)) {
                    if (value != null && char.class.isAssignableFrom(value.getClass())) {
                        field.setChar(source, (char) value);
                        result = true;
                    }
                } else if (boolean.class.isAssignableFrom(fieldType)) {
                    if (value != null && boolean.class.isAssignableFrom(value.getClass())) {
                        field.setBoolean(source, (boolean) value);
                        result = true;
                    }
                }
            } else if (fieldType.isArray()) {
                if (value != null) {
                    if (fieldType.isInstance(value)) {
                        field.set(source, value);
                    } else {
                        int len = Array.getLength(value);
                        Object realValue = Array.newInstance(fieldType.getComponentType(), len);
                        for (int i = 0; i < len; i++) {
                            Array.set(realValue, i, Array.get(value, i));
                        }
                        field.set(source, realValue);
                    }
                } else {
                    field.set(source, null);
                }
            } else {
                field.set(source, value);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //权限还原
                if (field != null) {
                    if (removeFinal && modifiersField != null) {
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                        modifiersField.setAccessible(false);
                    }
                    if (!Modifier.isPublic(modify) || Modifier.isFinal(modify)) {
                        field.setAccessible(false);
                    }
                }
            } catch (IllegalAccessException e) {
                //
            }
        }
        return result;
    }
}
