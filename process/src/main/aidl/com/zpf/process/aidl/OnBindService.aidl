package com.zpf.process.aidl;
import com.zpf.process.aidl.IHostHandler;
import com.zpf.process.aidl.IServiceHandler;

interface OnBindService {
    IServiceHandler onBind(in IHostHandler handler);
}