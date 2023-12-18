package com.zpf.apptest.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestRequest {
    private final ExecutorService service = new ThreadPoolExecutor(0, 16,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    final HashSet<IRequestInterceptor> interceptors = new HashSet<>();
    IResponseCache cacheManager;

    @Nullable
    public <T> Future<IResponse<T>> submit(@NonNull IRequest request, @NonNull Class<T> responseType) {
        //检查请求
        boolean intercepted = false;
        MutableResponse<T> response = new MutableResponse<>();
        synchronized (interceptors) {
            for (IRequestInterceptor interceptor : interceptors) {
                if (interceptor.shouldIntercept(request)) {
                    intercepted = true;
                    break;
                }
            }
        }
        if (intercepted) {
            response.success = false;
            response.code = -1;
            response.message = "intercepted";
            return new FailedFuture<T>(response);
        }
//        if (cacheManager != null) {
//            Object cacheObj = cacheManager.findCache(request.cacheKey());
//            responseType.
//            if(cacheObj!=null&&cacheObj.getClass()==responseType){
//
//            }
//        }
//
//
//        new FutureTask();
        //检查缓存
//        if (request.cacheKey() == null) {
//            request.path()
//        }

        //分发请求

        //执行请求

        //解析响应

        //更新缓存

        //回调监听
//        return job;
        return null;
    }

    private boolean checkRequest(@NonNull IRequest request) {
        //检查目标路径
        //检查token权限

        return false;

    }
}
