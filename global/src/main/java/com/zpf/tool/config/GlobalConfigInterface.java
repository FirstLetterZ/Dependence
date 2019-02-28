package com.zpf.tool.config;

import java.util.UUID;

/**
 * 用于全局执行，需要在应用刚启动时完成初始化
 * Created by ZPF on 2018/7/27.
 */
public interface GlobalConfigInterface {

    UUID getId();

    /**
     * @param object 需要处理的对象
     */
    void onObjectInit(Object object);

    /**
     * @param object     调用的对象
     * @param methodName 需要处理的方法
     * @param args       参数集合
     */
    Object invokeMethod(Object object, String methodName, Object... args);


    /**
     * @param target 目标对应的Class
     * @return 对应的全局单例
     */
    <T> T getGlobalInstance(Class<T> target);
}
