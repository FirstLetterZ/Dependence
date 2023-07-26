package com.zpf.apptest.request;

import androidx.annotation.Nullable;

import org.json.JSONObject;

/**
 * @author Created by ZPF on 2021/6/23.
 */
public interface IRequestCreator<T>  {
    IRequestCall<T>  create(@Nullable JSONObject params);
}