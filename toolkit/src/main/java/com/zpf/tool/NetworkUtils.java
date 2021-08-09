package com.zpf.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

public class NetworkUtils {

    public static boolean checkProxy(Context context) {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);

    }

    public static boolean checkVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void clearProxy() {
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");
    }

    @SuppressLint("MissingPermission")
    @NetworkState
    public static int getNetworkType(Context context) {
        if (null == context) {
            return NetworkState.NETWORK_NONE;
        }
        int typeCode = -1;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = null;
        if (connectivityManager != null) {
            try {
                activeNetInfo = connectivityManager.getActiveNetworkInfo();
                typeCode = activeNetInfo.getSubtype();
                if (!activeNetInfo.isAvailable()) {
                    return NetworkState.NETWORK_NONE;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != activeNetInfo) {
            if (!activeNetInfo.isAvailable()) {
                return NetworkState.NETWORK_NONE;
            } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkState.NETWORK_WIFI;
            }
        }
        if (typeCode == -1) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context
                    .TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                typeCode = telephonyManager.getNetworkType();
            }
        }
        if (typeCode > 0) {
            String subTypeName = null;
            if (activeNetInfo != null) {
                subTypeName = activeNetInfo.getSubtypeName();
            }
            switch (typeCode) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NetworkState.NETWORK_2G;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NetworkState.NETWORK_3G;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NetworkState.NETWORK_4G;
                default:
                    if ("TD-SCDMA".equalsIgnoreCase(subTypeName)
                            || "WCDMA".equalsIgnoreCase(subTypeName)
                            || "CDMA2000".equalsIgnoreCase(subTypeName)) {
                        return NetworkState.NETWORK_3G;
                    } else {
                        return NetworkState.NETWORK_UNKNOWN_MOBILE;
                    }
            }
        }
        return NetworkState.NETWORK_NONE;
    }

    @SuppressLint("MissingPermission")
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkinfo = null;
        try {
            networkinfo = connectivityManager.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return networkinfo != null && networkinfo.isAvailable();
    }

}
