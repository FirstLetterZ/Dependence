package com.zpf.api;

import androidx.annotation.DrawableRes;
import android.view.View;

/**
 * 图片加载
 * Created by ZPF on 2019/5/13.
 */
public interface IViewLoader {

    void fromStringUri(View targetView, String uri);

    void fromStringUri(View targetView, String uri, @DrawableRes int holderId);

    void fromStringUri(View targetView, String uri, @DrawableRes int holderId, @DrawableRes int failId);

    void fromSource(View targetView, @DrawableRes int redId);
}
