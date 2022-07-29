package com.snaggly.ksw_toolkit.util.commander

import android.content.Context

object AppStarter {
    fun launchAppById(appId: String?, context: Context) {
        context.startActivity(appId?.let { context.packageManager.getLaunchIntentForPackage(it) })
    }
}