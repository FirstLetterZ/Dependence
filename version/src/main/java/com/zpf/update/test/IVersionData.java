package com.zpf.update.test;

import androidx.annotation.Nullable;

public interface IVersionData {

    @Nullable
    String getResourceMd5();

    @Nullable
    String getResourceUrl();

    @Nullable
    String getVersionName();

    int getVersionCode();
}
