package com.zpf.aaa.service;

import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ICrossMessenger {
    String SERVICE_MESSAGE_NAME = "SERVICE_MESSAGE_NAME";
    String CLIENT_MESSAGE_NAME = "CLIENT_MESSAGE_NAME";
    String DATA_KEY_MESSENGER_NAME = "DATA_KEY_MESSENGER_NAME";

    boolean send(@NonNull String targetName, int what, @Nullable Bundle extraData);

    boolean send(@NonNull String target, @NonNull Message message);

    boolean resend(@NonNull String target);

    void addCallback(ICrossCallback crossCallback);

    void removeCallback(ICrossCallback crossCallback);
}