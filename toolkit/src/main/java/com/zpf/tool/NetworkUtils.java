package com.zpf.tool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthTdscdma;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtils {
    private static int mNetworkState = NetworkState.NETWORK_UNKNOWN;
    private static int mSignalStrength = Integer.MIN_VALUE;
    private static long cacheTime = 0L;
    private volatile static ConnectivityManager.NetworkCallback callback;

    @SuppressLint("MissingPermission")
    public static synchronized void checkNetworkCallbackRegistered(Context context) {
        final Context appContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && callback == null) {
            callback = new ConnectivityManager.NetworkCallback() {

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                        ) {
                            mNetworkState = NetworkState.NETWORK_WIFI;
                        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                        ) {
                            mNetworkState = getTelephonyNetworkType(appContext);
                        } else {
                            mNetworkState = NetworkState.NETWORK_UNKNOWN;
                        }
                    } else {
                        mNetworkState = NetworkState.NETWORK_NONE;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        mSignalStrength = networkCapabilities.getSignalStrength();
                    }
                    cacheTime = System.currentTimeMillis();
                }
            };
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                try {
                    manager.registerNetworkCallback(new NetworkRequest.Builder().build(), callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean checkProxy(Context context) {
        String proxyAddress;
        int proxyPort;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
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

    @NetworkState
    public static int getNetworkType(Context context, long cacheEffective) {
        if (mNetworkState != NetworkState.NETWORK_UNKNOWN) {
            if (System.currentTimeMillis() - cacheTime <= cacheEffective) {
                return mNetworkState;
            }
        }
        int type = getNetworkType(context);
        if (type != NetworkState.NETWORK_UNKNOWN) {
            mNetworkState = type;
            cacheTime = System.currentTimeMillis();
        }
        return type;
    }

    @SuppressLint("MissingPermission")
    @NetworkState
    public static int getNetworkType(Context context) {
        if (null == context) {
            return NetworkState.NETWORK_UNKNOWN;
        }
        int typeCode = -1;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = null;
        if (connectivityManager != null) {
            try {
                activeNetInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetInfo != null) {
                    if (!activeNetInfo.isAvailable()) {
                        return NetworkState.NETWORK_NONE;
                    }
                    typeCode = activeNetInfo.getSubtype();
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
        if (typeCode <= 0) {
            return NetworkState.NETWORK_UNKNOWN;
        }
        return parseTypeCode(typeCode);
    }

    @SuppressLint("MissingPermission")
    private static int getTelephonyNetworkType(Context context) {
        boolean missingPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
        if (missingPermission) {
            return 0;
        }

        int typeCode = 0;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                typeCode = telephonyManager.getDataNetworkType();
            } else {
                typeCode = telephonyManager.getNetworkType();
            }
        }
        if (typeCode <= 0) {
            return NetworkState.NETWORK_UNKNOWN;
        }
        return parseTypeCode(typeCode);
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

    private static int parseTypeCode(int netType) {
        int result;
        switch (netType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                result = NetworkState.NETWORK_2G;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                result = NetworkState.NETWORK_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_IWLAN:
            case 19:
                result = NetworkState.NETWORK_4G;
                break;
            case TelephonyManager.NETWORK_TYPE_NR:
                result = NetworkState.NETWORK_5G;
                break;
            default:
                result = NetworkState.NETWORK_UNKNOWN;
        }

        return result;
    }

    public static int getSignalStrength(Context context, long cacheEffective) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && mSignalStrength != Integer.MIN_VALUE) {
            if (System.currentTimeMillis() - cacheTime <= cacheEffective) {
                return mSignalStrength;
            }
        }
        int typeCode = getNetworkType(context, cacheEffective);
        int strength;
        if (typeCode == NetworkState.NETWORK_WIFI) {
            strength = getWifiSignalStrength(context);
        } else if (typeCode == NetworkState.NETWORK_UNKNOWN || typeCode == NetworkState.NETWORK_NONE) {
            strength = 0;
        } else {
            strength = getMobileSignalStrength(context);
        }
        if (strength != 0) {
            mSignalStrength = strength;
        }
        return strength;
    }

    public static int getWifiSignalStrength(Context context) {
        if (context == null) {
            return 0;
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return 0;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getRssi();
        } else {
            return 0;
        }
    }

    @SuppressLint("MissingPermission")
    public static int getMobileSignalStrength(Context context) {
        boolean missingPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (missingPermission) {
            return 0;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList = tm.getAllCellInfo();
        if (null == cellInfoList) {
            return 0;
        }
        int result = 0;
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoGsm) {
                CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                result = cellSignalStrengthGsm.getDbm();
            } else if (cellInfo instanceof CellInfoCdma) {
                CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                result = cellSignalStrengthCdma.getDbm();
            } else if (cellInfo instanceof CellInfoWcdma) {
                CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                result = cellSignalStrengthWcdma.getDbm();
            } else if (cellInfo instanceof CellInfoLte) {
                CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                result = cellSignalStrengthLte.getDbm();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (cellInfo instanceof CellInfoNr) {
                    CellSignalStrength cellSignalStrengthNr = ((CellInfoNr) cellInfo).getCellSignalStrength();
                    result = cellSignalStrengthNr.getDbm();
                } else if (cellInfo instanceof CellInfoTdscdma) {
                    CellSignalStrengthTdscdma cellSignalStrengthTdscdma = ((CellInfoTdscdma) cellInfo).getCellSignalStrength();
                    result = cellSignalStrengthTdscdma.getDbm();
                }
            }
            if (result != 0) {
                break;
            }
        }
        return result;
    }
}
