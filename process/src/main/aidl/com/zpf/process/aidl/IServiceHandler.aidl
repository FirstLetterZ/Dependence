package com.zpf.process.aidl;
import android.os.Bundle;
import com.zpf.process.aidl.ICallback;

interface IServiceHandler {

    void invoke(String event,in Bundle params,in ICallback callback);

    void startToWork(String clientId,in Bundle params);

    void stopAllWork();

    String getCurrentClient();
}