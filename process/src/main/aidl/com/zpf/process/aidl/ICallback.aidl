package com.zpf.process.aidl;
import android.os.Bundle;

interface ICallback{
    void callback(String event,in Bundle params);
}