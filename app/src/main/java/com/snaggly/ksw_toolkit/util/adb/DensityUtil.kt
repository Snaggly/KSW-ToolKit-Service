package com.snaggly.ksw_toolkit.util.adb

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.snaggly.ksw_toolkit.util.commander.AppStarter
import kotlin.math.min

object DensityUtil {
    fun turnOnTabletMode(context: Context) {
        val displayMetrics = context.resources.displayMetrics
        val tabletDpi = Integer.max(
            (min(
                displayMetrics.widthPixels,
                displayMetrics.heightPixels
            ) * 160) / Integer.max(600, 320), 120
        )
        try {
            AdbManager.sendCommand("wm density $tabletDpi", context)
        } catch (_: Exception) { }
    }

    fun turnOffTabletMode(context: Context) {
        val dpi : Int =
            if (context.resources.displayMetrics.heightPixels > 480)
                240
            else
                160

        try {
            AdbManager.sendCommand("wm density $dpi", context)
        } catch (_: Exception) { }
    }
}