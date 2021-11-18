package com.zpf.apptest.request;

import androidx.annotation.NonNull;

import org.json.JSONObject;

/**
 * @author Created by ZPF on 2021/6/23.
 */
public interface IRequestCreator<T>  {
    IRequestCall<T>  create(@NonNull JSONObject params);
}