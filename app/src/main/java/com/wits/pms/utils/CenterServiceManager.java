package com.wits.pms.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class CenterServiceManager {
    private static int uid = -1;

    public static int getUid(Context context) {
        if (uid == -1) {
            try {
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo("com.wits.pms", 0);
                uid = appInfo.uid;
            } catch (Exception e) {
                Log.e("CenterServiceManager", "Could not get uid of CenterService\n" + e);
            }
        }
        return uid;
    }
}
