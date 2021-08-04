package com.zpf.process.aidl;
import android.os.Bundle;
import com.zpf.process.aidl.ICallback;

interface IHostHandler{
    void handleMessage(String clientId,String event,in Bundle params,in ICallback callback);
}