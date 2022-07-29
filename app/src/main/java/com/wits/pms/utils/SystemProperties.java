package com.wits.pms.utils;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;

@SuppressLint("PrivateApi")
public class SystemProperties {
    public static void set(String key, String val) {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method set = systemProperties.getMethod("set", String.class, String.class);
            set.invoke(systemProperties, key, val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method set = systemProperties.getMethod("get", String.class);
            return (String) set.invoke(systemProperties, key);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
