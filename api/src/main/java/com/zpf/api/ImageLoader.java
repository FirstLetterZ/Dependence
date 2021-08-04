package com.zpf.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.net.Uri;
import android.view.View;

import org.json.JSONObject;

import java.io.File;

/**
 * 图片加载
 * Created by ZPF on 2019/5/13.
 */
public interface ImageLoader {

    Object load(@NonNull View targetView, @Nullable String path, @Nullable JSONObject args);

    Object load(@NonNull View targetView, @Nullable Uri uri, @Nullable JSONObject args);

    Object save(@NonNull String path, @NonNull String localPath, @Nullable JSONObject args,
                @Nullable OnDataResultListener<File> listener);

    Object share(@NonNull String path, @Nullable JSONObject args);

    Object share(@NonNull Uri uri, @Nullable JSONObject args);
}
