package com.snaggly.ksw_toolkit.util.applist

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

@SuppressLint("QueryPermissionsNeeded")
class AppsLister (context: Context) {
    private val appsList = ArrayList<AppInfo>()

    init {
        val apps = context.packageManager.getInstalledPackages(0)

        for (packageInfo in apps){
            if (context.packageManager.getLaunchIntentForPackage(packageInfo.packageName) == null) {
                continue;
            }
            var thisAppInfo = AppInfo(
                    packageInfo.applicationInfo.loadLabel(context.packageManager) as String,
                    packageInfo.packageName,
                    packageInfo.applicationInfo.loadIcon(context.packageManager)
            )

            appsList.add(thisAppInfo)
        }
    }

    fun getNumOfApps() : Int{
        return appsList.size
    }

    fun getAppsList() : ArrayList<AppInfo> {
        return appsList
    }
}