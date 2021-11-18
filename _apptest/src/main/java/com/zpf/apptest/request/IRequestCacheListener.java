package com.zpf.apptest.request;

import androidx.annotation.NonNull;

/**
 * @author Created by ZPF on 2021/6/24.
 */
public interface IRequestCacheListener {
    boolean onFindCache(@NonNull String cache);
}
