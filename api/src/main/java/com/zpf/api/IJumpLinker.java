
package com.zpf.api;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

public interface IJumpLinker {
    boolean jumpByName(@NonNull Context context, @NonNull String name, @Nullable JSONObject params);

    boolean jumpByUrl(@NonNull Context context, @NonNull String url);
}