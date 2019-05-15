package com.zpf.frame;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ZPF on 2019/5/13.
 */
public interface INavigator<T> {

    void push(T target, Bundle params, int requestCode);

    void push(T target, Bundle params);

    void push(T target);

    void poll(int resultCode, Intent data);

    void poll();

    void pollUntil(T target, int resultCode, Intent data);

    void pollUntil(T target);

    void remove(T target, int resultCode, Intent data);

    void remove(T target);
}
