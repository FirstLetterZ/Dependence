package com.zpf.tool.fingerprint;

/**
 * Created by ZPF on 2019/2/12.
 */
public interface FingerprintAuthListener {

    void onError(@FingerprintAuthState int stateCode, int errCode, CharSequence errMsg);

    void onFail(@FingerprintAuthState int stateCode, int failCode, CharSequence failMsg);

    void onSuccess();

    void onStart();

    void onCancel();

}
