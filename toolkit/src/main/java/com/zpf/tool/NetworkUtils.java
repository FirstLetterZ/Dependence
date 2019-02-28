package com.zpf.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

@SuppressLint("MissingPermission")
public class NetworkUtils {

    @NetworkState
    public static int getNetworkType(Context context) {
        if (null == context) {
            return NetworkState.NETWORK_NONE;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager) {
            return NetworkState.NETWORK_NONE;
        }

        NetworkInfo activeNetInfo = null;
        try {
            activeNetInfo = connectivityManager.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == activeNetInfo || !activeNetInfo.isAvailable()) {
            return NetworkState.NETWORK_NONE;
        } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NetworkState.NETWORK_WIFI;
        } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            final NetworkInfo.State state = activeNetInfo.getState();
            final String subTypeName = activeNetInfo.getSubtypeName();
            if (null != state) {
                switch (activeNetInfo.getSubtype()) {
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
                        if (subTypeName.equalsIgnoreCase("TD-SCDMA")
                                || subTypeName.equalsIgnoreCase("WCDMA")
                                || subTypeName.equalsIgnoreCase("CDMA2000")) {
                            return NetworkState.NETWORK_3G;
                        } else {
                            return NetworkState.NETWORK_UNKNOWN_MOBILE;
                        }
                }
            }
        }
        return NetworkState.NETWORK_NONE;
    }

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
