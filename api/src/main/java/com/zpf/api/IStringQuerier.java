package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IStringQuerier extends IQuerier<String> {
    @Nullable
    String query(@NonNull String condition);
}