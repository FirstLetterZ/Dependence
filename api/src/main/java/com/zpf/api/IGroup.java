package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * Created by ZPF on 2022/5/30
 */
public interface IGroup {

    boolean remove(@NonNull Object obj, @Nullable Type asType);

    boolean add(@NonNull Object obj, @Nullable Type asType);

    int size(@Nullable Type asType);
}