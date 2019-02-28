package com.zpf.tool.config;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ZPF on 2018/10/12.
 */
public class GlobalConfigImpl implements GlobalConfigInterface {
    private GlobalConfigInterface realGlobalConfig;
    private final HashMap<UUID, GlobalConfigInterface> configCollection = new HashMap<>();
    private boolean isDebug = true;
    private boolean hasInit = false;
    private UUID uuid = UUID.randomUUID();
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

    public void init(Application application, GlobalConfigInterface globalConfig) {
        AppContext.init(application);
        realGlobalConfig = globalConfig;
        isDebug = (application.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        hasInit = true;
    }

    public void add(GlobalConfigInterface globalConfig) {
        configCollection.put(globalConfig.getId(), globalConfig);
    }

    public void remove(UUID id) {
        configCollection.remove(id);
    }

    public boolean isDebug() {
        return hasInit && isDebug;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public void onObjectInit(Object object) {
        if (realGlobalConfig != null) {
            realGlobalConfig.onObjectInit(object);
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
                    for (GlobalConfigInterface config : configCollection.values()) {
                        result = config.invokeMethod(object, methodName, args);
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
