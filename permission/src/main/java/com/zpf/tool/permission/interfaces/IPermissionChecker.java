package com.zpf.tool.permission.interfaces;


import android.content.Context;

/**
 * @author Created by ZPF on 2021/6/18.
 */
public interface IPermissionChecker {
    //检查传入对象是否为有效
    boolean shouldHandleRequest(Object requester);

    //是否可以使用系统弹窗请求权限
    boolean shouldShowRequestPermissionRationale(String p);

    //请求权限
    void requestPermissions(String[] p, int code);

    //用来检查是否拥有权限
    Context getContext();
}
