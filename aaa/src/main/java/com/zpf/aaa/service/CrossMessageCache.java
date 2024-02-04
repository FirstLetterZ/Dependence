package com.zpf.aaa.service;

import android.os.Message;

public class CrossMessageCache {
    public final String target;
    public final Message message;
    public CrossMessageCache(String target, Message message) {
        this.target = target;
        this.message = message;
    }
}
