package com.snaggly.ksw_toolkit.util.applist

import android.content.Context

object AppStarter {
    fun launchAppById(appId: String, context: Context) {
        context.startActivity(context.packageManager.getLaunchIntentForPackage(appId))
    }
}