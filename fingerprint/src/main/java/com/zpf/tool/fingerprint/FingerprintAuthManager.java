package com.zpf.tool.fingerprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by ZPF on 2019/2/12.
 */
public class FingerprintAuthManager {
    private FingerprintManager mManager;
    private FingerprintAuthListener mAuthListener;
    private FingerprintManager.CryptoObject mCryptoObject;
    private android.os.CancellationSignal mCancellationSignal;
    public static final int CODE_NO_MSG = -99;
    private int mRetryTime;
    private int mMaxRetryTime = 5;
    private volatile boolean waitInit = false;
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    @FingerprintAuthState
    private int mState;

    public FingerprintAuthManager(Context context) {
        this(context, true);
    }

    public FingerprintAuthManager(Context context, final boolean ignoreCryptoInitFail) {
        mManager = getFingerprintManager(context);
        mState = FingerprintAuthState.AUTH_INITING;
        new CryptoObjectHelper(new CryptoObjectHelper.OnPreparedListener() {
            @Override
            public void onPrepared(FingerprintManager.CryptoObject cryptoObject, int stateCode) {
                mCryptoObject = cryptoObject;
                if (mCryptoObject != null && mManager != null) {
                    if (ignoreCryptoInitFail || stateCode == CryptoObjectHelper.INITIALIZATION_SUCCESS) {
                        mState = FingerprintAuthState.AUTH_INIT_SUCCESS;
                    } else {
                        mState = FingerprintAuthState.AUTH_INIT_FAIL;
                    }
                } else {
                    mState = FingerprintAuthState.AUTH_INIT_FAIL;
                }
                if (waitInit) {
                    waitInit = false;
                    authenticate(mAuthListener);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void authenticate(final FingerprintAuthListener listener) {
        if (listener == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mState = FingerprintAuthState.AUTH_NOT_SUPPORT;
            listener.onError(mState, CODE_NO_MSG, null);
        } else {
            if (mState == FingerprintAuthState.AUTH_INIT_FAIL) {
                listener.onError(mState, CODE_NO_MSG, null);
                return;
            }
            if (mManager == null) {
                mState = FingerprintAuthState.AUTH_MANAGER_MISSING;
                callBackOnError(listener, CODE_NO_MSG, null);
                return;
            }
            if (!mManager.isHardwareDetected()) {
                mState = FingerprintAuthState.AUTH_NOT_SUPPORT;
                callBackOnError(listener, CODE_NO_MSG, null);
                return;
            }
            if (!mManager.hasEnrolledFingerprints()) {
                mState = FingerprintAuthState.AUTH_NO_FINGERPRINTS;
                callBackOnError(listener, CODE_NO_MSG, null);
                return;
            }
            mAuthListener = listener;
            if (mState == FingerprintAuthState.AUTH_INITING) {
                waitInit = true;
            } else {
                if (mCancellationSignal == null) {
                    mCancellationSignal = new android.os.CancellationSignal();
                }
                mState = FingerprintAuthState.AUTH_START;
                listener.onStart();
                mManager.authenticate(mCryptoObject, mCancellationSignal, 0, authCallback, null);
            }
        }
    }

    public void setMaxRetryTime(int maxRetryTime) {
        this.mMaxRetryTime = maxRetryTime;
    }

    public int getMaxRetryTime() {
        return this.mMaxRetryTime;
    }

    private Runnable mRetryRunnable = new Runnable() {
        @Override
        public void run() {
            if (mState != FingerprintAuthState.AUTH_CANCEL) {
                authenticate(mAuthListener);
            }
        }
    };

    private void retry() {
        mainHandler.removeCallbacks(mRetryRunnable);
        mainHandler.postDelayed(mRetryRunnable, 300);
    }

    public void cancelAuth() {
        if (mCancellationSignal != null && mState != FingerprintAuthState.AUTH_CANCEL) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
        mState = FingerprintAuthState.AUTH_CANCEL;
        mRetryTime = 0;
        if (mAuthListener != null) {
            mAuthListener.onCancel();
            mAuthListener = null;
        }
    }

    private void callBackOnError(FingerprintAuthListener listener, int errCode, CharSequence errMsg) {
        if (listener != null) {
            listener.onError(mState, errCode, errMsg);
        }
        mAuthListener = null;
        mRetryTime = 0;
    }

    private FingerprintManager.AuthenticationCallback authCallback = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            if (mAuthListener == null) {
                return;
            }
            mState = FingerprintAuthState.AUTH_ERROR;
            callBackOnError(mAuthListener, errorCode, errString);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            if (mAuthListener == null) {
                return;
            }
            if (mRetryTime < mMaxRetryTime) {
                mRetryTime++;
                mState = FingerprintAuthState.AUTH_FAIL;
                mAuthListener.onFail(mState, helpCode, helpString);
                retry();
            } else {
                mState = FingerprintAuthState.AUTH_OUT_RETRY;
                callBackOnError(mAuthListener, helpCode, helpString);
            }
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            if (mAuthListener == null) {
                return;
            }
            mState = FingerprintAuthState.AUTH_SUCCESS;
            mAuthListener.onSuccess();
            mRetryTime = 0;
            mAuthListener = null;
        }

        @Override
        public void onAuthenticationFailed() {
            if (mAuthListener == null) {
                return;
            }
            if (mRetryTime < mMaxRetryTime) {
                mRetryTime++;
                mState = FingerprintAuthState.AUTH_FAIL;
                mAuthListener.onFail(mState, CODE_NO_MSG, null);
                retry();
            } else {
                mState = FingerprintAuthState.AUTH_OUT_RETRY;
                callBackOnError(mAuthListener, CODE_NO_MSG, null);
            }
        }
    };

    public static FingerprintManager getFingerprintManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                    return context.getSystemService(FingerprintManager.class);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}