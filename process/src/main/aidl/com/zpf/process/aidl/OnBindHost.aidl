package com.zpf.process.aidl;
import com.zpf.process.aidl.IHostHandler;
import com.zpf.process.aidl.IServiceHandler;

interface OnBindHost {
    IHostHandler onBind(int serviceId,in IServiceHandler handler);
}