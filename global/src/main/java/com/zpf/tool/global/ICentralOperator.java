package com.zpf.tool.global;

/**
 * @author Created by ZPF on 2021/7/7.
 */
public interface ICentralOperator {

    String getId();

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
     * @param qualifier 限定符，用于获取相同class的不同实现
     * @return 对应的全局单例
     */
    <T> T getInstance(Class<T> target, String qualifier);

    /**
     * 上面方法中 qualifier 为null时的默认实现
     */
    <T> T getInstance(Class<T> target);
}
