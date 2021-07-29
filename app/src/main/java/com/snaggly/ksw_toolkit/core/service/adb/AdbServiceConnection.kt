package com.snaggly.ksw_toolkit.core.service.adb

import android.content.Context
import com.snaggly.ksw_toolkit.util.adb.AdbManager

object AdbServiceConnection {
    private var adbShellListener : ShellObserver? = null

    fun connect(context: Context) {
        AdbManager.connect(context, "shell:", object : AdbManager.OnAdbShellDataReceived {
            override fun onDataReceived(text: String) {
                adbShellListener?.update(text)
            }
        })
    }

    fun disconnect() {
        AdbManager.disconnect()
    }

    fun sendCommand(command: String) {
        AdbManager.sendCommand(command)
    }

    fun stopKsw() {
        sendCommand("am stopservice --user 0 com.wits.pms/com.wits.pms.mcu.McuService\nappops set com.wits.pms SYSTEM_ALERT_WINDOW deny")
    }

    fun startKsw() {
        sendCommand("am startservice --user 0 com.wits.pms/com.wits.pms.mcu.McuService\nappops set com.wits.pms SYSTEM_ALERT_WINDOW allow")
    }

    fun sendKeyEvent(code: Int) {
        sendCommand("input keyevent $code")
    }

    fun startApp(appId: String) {
        sendCommand("monkey -p $appId -c android.intent.category.LAUNCHER 1")
    }

    fun registerShellListener(listener: ShellObserver) {
        adbShellListener = listener
    }

    fun unregisterShellListener(listener: ShellObserver) {
        adbShellListener = listener
    }
}