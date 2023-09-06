package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

public interface IEventTracker {
    void onEvent(@NonNull String name, @Nullable Map<String, Object> params);
}