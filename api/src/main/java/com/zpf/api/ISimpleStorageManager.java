package com.zpf.api;

import androidx.annotation.NonNull;

public interface ISimpleStorageManager extends IStorageManager<String> {

    String getString(@NonNull String key);

    boolean getBoolean(@NonNull String key);

    int getInt(@NonNull String key);

    long getLong(@NonNull String key);

    float getFloat(@NonNull String key);

    double getDouble(@NonNull String key);
}
