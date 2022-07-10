package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

public interface IStateManager {
    boolean setState(@NonNull JSONObject jsonObject);

    boolean setState(@NonNull String name,@Nullable Object value);

    @Nullable
    Object getState(@NonNull String name);
}
