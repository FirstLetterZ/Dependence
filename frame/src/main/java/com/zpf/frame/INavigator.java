package com.zpf.frame;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZPF on 2019/5/13.
 */
public interface INavigator<T> {

    void push(@NonNull T target, @Nullable Intent params, int requestCode);

    void push(@NonNull T target, @Nullable Intent params);

    void push(@NonNull T target);

    void pop(int resultCode, @Nullable Intent data);

    void pop();

    void popToRoot(@Nullable Intent data);

    boolean popTo(@NonNull T target, @Nullable Intent data);

    void replace(@NonNull T target, @Nullable Intent params);

    boolean remove(@NonNull T target);
}
