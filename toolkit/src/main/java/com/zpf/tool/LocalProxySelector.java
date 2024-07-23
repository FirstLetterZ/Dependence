package com.zpf.tool;

import android.content.SharedPreferences;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

public class LocalProxySelector extends ProxySelector {
    public static final String PROXY_CONFIG_CACHE_KEY = "proxy_selector_config_key";
    private boolean enable = false;
    private final SharedPreferences sharedPreferences;
    private Proxy proxy;

    public LocalProxySelector(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        setProxyConfig(sharedPreferences.getString(PROXY_CONFIG_CACHE_KEY, null));
    }

    @Override
    public List<Proxy> select(URI uri) {
        return null;
    }
    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

    }

    public boolean isEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean setProxyConfig(String host, int port) {
        InetSocketAddress proxyAddress = null;
        try {
            proxyAddress = new InetSocketAddress(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (proxyAddress == null) {
            return false;
        }
        proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
        return true;
    }

    public void setProxyConfig(String configStr) {
        String[] configs = null;
        if (configStr != null) {
            configs = configStr.split(":");
        }
        proxy = null;
        if (configs != null && configs.length > 0) {
            enable = "1".endsWith(configs[0]);
            if (configs.length > 2) {
                try {
                    InetSocketAddress proxyAddress = new InetSocketAddress(configs[1], Integer.parseInt(configs[2]));
                    proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            enable = false;
        }
    }

    public void save(boolean now) {
        String configStr = null;
        if (proxy != null) {
            SocketAddress address = proxy.address();
            if (address != null) {
                configStr = address.toString().replace("/", "");
            }
        }
        if (configStr == null) {
            configStr = "";
        }
        if (enable) {
            configStr = "1:" + configStr;
        } else {
            configStr = "0:" + configStr;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit().putString(PROXY_CONFIG_CACHE_KEY, configStr);
        if (now) {
            editor.commit();
        } else {
            editor.apply();
        }
    }
}
