package com.zpf.apptest.request;

import androidx.annotation.NonNull;

/**
 * @author Created by ZPF on 2021/6/24.
 */
public interface IResponseListener<T> {

    void onResponse(@NonNull IRequestCall<T> call, @NonNull IResponse<T> response);
}
