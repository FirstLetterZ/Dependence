package com.zpf.apptest.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IResponseCache {
    @Nullable
    Object findCache(@NonNull String key);

    void updateCache(@NonNull String key, @Nullable Object newCache);
}
