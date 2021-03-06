package com.zpf.tool.config;

import android.app.Application;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ZPF on 2018/10/12.
 */
public class GlobalConfigImpl implements GlobalConfigInterface {
    private GlobalConfigInterface realGlobalConfig;
    private final HashMap<String, GlobalConfigInterface> configCollection = new HashMap<>();
    private String uuid = UUID.randomUUID().toString();
    private static volatile GlobalConfigImpl mInstance;

    private GlobalConfigImpl() {
    }

    public static GlobalConfigImpl get() {
        if (mInstance == null) {
            synchronized (GlobalConfigImpl.class) {
                if (mInstance == null) {
                    mInstance = new GlobalConfigImpl();
                }
            }
        }
        return mInstance;
    }

    public void setDefConfig(GlobalConfigInterface globalConfig) {
        realGlobalConfig = globalConfig;
    }

    public GlobalConfigInterface getDefConfig() {
        return realGlobalConfig;
    }

    public void init(Application application, GlobalConfigInterface globalConfig) {
        AppContext.init(application);
        realGlobalConfig = globalConfig;
    }

    public void add(GlobalConfigInterface globalConfig) {
        configCollection.put(globalConfig.getId(), globalConfig);
    }

    public void remove(String id) {
        configCollection.remove(id);
    }

    public boolean isDebug() {
        return AppContext.isDebuggable();
    }

    @Override
    public String getId() {
        return uuid;
    }

    @Override
    public void onObjectInit(Object object) {
        if (realGlobalConfig != null) {
            realGlobalConfig.onObjectInit(object);
        }
        if (configCollection.size() > 0) {
            synchronized (configCollection) {
                if (configCollection.size() > 0) {
                    for (GlobalConfigInterface config : configCollection.values()) {
                        config.onObjectInit(object);
                    }
                }
            }
        }
    }

    @Override
    public Object invokeMethod(Object object, String methodName, Object... args) {
        Object result = null;
        if (realGlobalConfig != null) {
            result = realGlobalConfig.invokeMethod(object, methodName, args);
        }
        if (configCollection.size() > 0) {
            synchronized (configCollection) {
                if (configCollection.size() > 0) {
                    Object temp;
                    for (GlobalConfigInterface config : configCollection.values()) {
                        temp = config.invokeMethod(object, methodName, args);
                        if (temp != null) {
                            result = temp;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public <T> T getGlobalInstance(Class<T> target) {
        T result = null;
        if (realGlobalConfig != null) {
            result = realGlobalConfig.getGlobalInstance(target);
        }
        if (result == null && configCollection.size() > 0) {
            synchronized (configCollection) {
                if (configCollection.size() > 0) {
                    for (GlobalConfigInterface config : configCollection.values()) {
                        result = config.getGlobalInstance(target);
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

}
