package com.snaggly.ksw_toolkit.core.service.adb

import android.content.Context
import com.snaggly.ksw_toolkit.BuildConfig
import com.snaggly.ksw_toolkit.core.service.CoreService
import com.snaggly.ksw_toolkit.util.adb.AdbManager

object AdbServiceConnection {
    fun stopKsw(context: Context) {
        AdbManager.sendCommand("am stopservice --user 0 com.wits.pms/com.wits.pms.mcu.McuService\nappops set com.wits.pms SYSTEM_ALERT_WINDOW deny", context)
    }

    fun startKsw(context: Context) {
        AdbManager.sendCommand("am startservice --user 0 com.wits.pms/com.wits.pms.mcu.McuService\nappops set com.wits.pms SYSTEM_ALERT_WINDOW allow", context)
    }

    fun startThisService(context: Context) {
        AdbManager.sendCommand("am startservice --user 0 ${BuildConfig.APPLICATION_ID}/${CoreService::class.java.name}", context)
    }
}