
package com.zpf.api;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

public interface IJumpLinker {
    boolean jumpByName(@NonNull Context context, @NonNull String name, @Nullable Map<String, Object> params);

    boolean jumpByUrl(@NonNull Context context, @NonNull String url);
}