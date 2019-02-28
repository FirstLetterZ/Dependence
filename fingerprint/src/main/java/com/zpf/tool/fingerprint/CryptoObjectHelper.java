package com.zpf.tool.fingerprint;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.lang.ref.WeakReference;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * 创建指纹校验
 * Created by ZPF on 2019/2/12.
 */
@TargetApi(Build.VERSION_CODES.M)
public class CryptoObjectHelper {
    private static final String KEY_NAME = "crypto_object_key";
    private static final String KEYSTORE_NAME = "AndroidKeyStore";
    private FingerprintManager.CryptoObject mCryptoObject;
    private KeyStore mKeyStore;
    private Cipher mCipher;
    //初始化状态
    public static final int INITIALIZATION_FAIL = -2;
    public static final int CONSTRUCTOR_ERROR = -1;
    public static final int UNINITIALIZED = 0;
    public static final int INITIALIZING = 1;
    public static final int INITIALIZATION_SUCCESS = 2;
    //初始化状态
    private volatile int mInitState;
    private WeakReference<OnPreparedListener> waitPreparedListener;

    public CryptoObjectHelper() {
        this(null);
    }

    public CryptoObjectHelper(OnPreparedListener listener) {
        mInitState = UNINITIALIZED;
        try {
            mKeyStore = KeyStore.getInstance(KEYSTORE_NAME);
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            mCryptoObject = new FingerprintManager.CryptoObject(mCipher);
            if (checkCacheCipher()) {
                invokeListener(listener);
            } else {
                init(listener);
            }
        } catch (Throwable e) {
            mKeyStore = null;
            mCipher = null;
            mCryptoObject = null;
            mInitState = CONSTRUCTOR_ERROR;
            invokeListener(listener);
        }
    }

    public void getCryptoObject(OnPreparedListener listener) {
        if (listener != null) {
            if (mInitState == UNINITIALIZED) {
                init(listener);
            } else if (mInitState == INITIALIZING) {
                waitPreparedListener = new WeakReference<OnPreparedListener>(listener);
            } else {
                invokeListener(listener);
            }
        }
    }

    public synchronized void init(final OnPreparedListener listener) {
        if (mKeyStore == null || mCipher == null || mCryptoObject == null) {
            mInitState = INITIALIZATION_FAIL;
            invokeListener(listener);
        } else {
            mInitState = INITIALIZING;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mKeyStore.load(null);
                        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_NAME);
                        keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                                KeyProperties.PURPOSE_ENCRYPT |
                                        KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                                .setUserAuthenticationRequired(true)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                                .build());
                        keyGenerator.generateKey();
                        if (checkCacheCipher()) {
                            invokeListener(listener);
                        } else {
                            mInitState = INITIALIZATION_FAIL;
                            invokeListener(listener);
                        }
                    } catch (Exception e) {
                        mInitState = INITIALIZATION_FAIL;
                        invokeListener(listener);
                    }
                }
            }).start();
        }
    }

    private boolean checkCacheCipher() {
        try {
            if (mKeyStore.isKeyEntry(KEY_NAME)) {
                mKeyStore.load(null);
                SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
                mCipher.init(Cipher.ENCRYPT_MODE, key);
                mInitState = INITIALIZATION_SUCCESS;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void invokeListener(OnPreparedListener listener) {
        if (listener != null) {
            listener.onPrepared(mCryptoObject, mInitState);
        }
        if (waitPreparedListener != null) {
            OnPreparedListener waitListener = waitPreparedListener.get();
            if (waitListener != null) {
                waitListener.onPrepared(mCryptoObject, mInitState);
            }
            waitPreparedListener = null;
        }
    }

    public interface OnPreparedListener {
        void onPrepared(FingerprintManager.CryptoObject cryptoObject, int stateCode);
    }

}
