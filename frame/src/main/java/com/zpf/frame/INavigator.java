package com.zpf.frame;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ZPF on 2019/5/13.
 */
public interface INavigator<T> {

    void push(@NonNull T target, @Nullable Bundle params, int requestCode);

    void push(@NonNull T target, @Nullable Bundle params);

    void push(@NonNull T target);

    void poll(int resultCode, @Nullable Intent data);

    void poll();

    boolean pollUntil(@NonNull T target, @Nullable Intent data);

    boolean pollUntil(@NonNull T target);

    boolean remove(@NonNull T target);
}
