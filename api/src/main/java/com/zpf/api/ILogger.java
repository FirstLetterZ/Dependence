package com.zpf.api;

import androidx.annotation.IntRange;
import android.util.Log;

/**
 * 日志打印
 * Created by ZPF on 2019/2/28.
 */
public interface ILogger {
    void log(@IntRange(from = Log.VERBOSE, to = Log.ASSERT) int priority, String tag, String content);
}
