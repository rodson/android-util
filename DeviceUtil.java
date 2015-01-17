/**
 * Copyright © 2015 CVTE. All Rights Reserved.
 */
package com.cvte.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * @author Rodson
 * @description Utils for getting information of device.
 * @date 12/29/14
 * @since 1.0
 */
public class DeviceUtil {

    /**
     * Get the MAC address.
     *
     * @param context The context of the application
     * @return Return device's mac address
     */
    public static String getDeviceMac(Context context) {
        // Use wifi manager to get mac address
        android.net.wifi.WifiManager wifi =
                (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String deviceMac = wifi.getConnectionInfo().getMacAddress();
        if (!TextUtils.isEmpty(deviceMac)) {
            return deviceMac;
        }

        // Get ethernet mac address
        deviceMac = getMACAddress("eth0");
        if (!TextUtils.isEmpty(deviceMac)) {
            return deviceMac;
        }

        // Get wifi mac address
        deviceMac = getMACAddress("wlan0");
        if (!TextUtils.isEmpty(deviceMac)) {
            return deviceMac;
        }

        // Get other mac address
        deviceMac = getMACAddress(null);
        if (!TextUtils.isEmpty(deviceMac)) {
            return deviceMac;
        }

        return deviceMac;
    }

    /**
     * Get device id(if null, use mac. If mac is null either, use android id)
     *
     * @param context The context of the application
     * @return Return device's id
     */
    public static String getDeviceId(Context context) {
        android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = getDeviceMac(context);

            if (TextUtils.isEmpty(deviceId)) {
                deviceId = android.provider.Settings.Secure.getString(
                        context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
            }
        }
        return deviceId;
    }

    private static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                        continue;
                    }
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
            // for now eat exceptions
        }
        return "";
    }

    /**
     * Get device resolution that describe the pixels value of this display.
     *
     * @param context The context of the application.
     * @return The resolution with the with*height format.
     */
    public static String getScreenResolution(Context context) {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager)
                    context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            return "" + width + "*" + height;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}
