package com.zpf.apptest.task;

/**
 * @author Created by ZPF on 2021/10/16.
 */
public abstract class AbsTaskCallback<R> implements ITaskCallback {

    private final boolean nullable;
    private final int[] successCode;

    public AbsTaskCallback(boolean nullable, int[] successCode) {
        this.nullable = nullable;
        this.successCode = successCode;
    }

    @Override
    public final void onResult(int type, Object params, int code, Object data, String message) {
        if (data == null) {
            if (nullable) {
                onResult(isSuccess(code), code, null, message);
            } else {
                onResult(false, -99, null, "The result cannot be null.");
            }
        } else {
            R res = null;
            try {
                res = ((R) data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (res == null) {
                onResult(false, -98, null, "Type error. Conversion failed");
            } else {
                onResult(isSuccess(code), code, res, message);
            }
        }
    }

    protected boolean isSuccess(int code) {
        if (successCode == null) {
            return true;
        }
        for (int c : successCode) {
            if (c == code) {
                return true;
            }
        }
        return false;
    }

    abstract void onResult(boolean success, int code, R data, String message);
}
