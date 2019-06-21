package com.zpf.api;

public interface IClassLoader {

    Object newInstance(String name, Object... args);

    Class<?> getClass(String name);

    void addLoader(IClassLoader loader);

    void removeLoader(IClassLoader loader);

}
