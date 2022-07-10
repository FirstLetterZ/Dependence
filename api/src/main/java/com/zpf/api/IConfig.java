package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IConfig {
    @Nullable
    String getConfig(@NonNull String name);
}
