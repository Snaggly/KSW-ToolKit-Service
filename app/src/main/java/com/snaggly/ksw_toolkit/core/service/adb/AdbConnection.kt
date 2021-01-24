package com.snaggly.ksw_toolkit.core.service.adb

import android.content.Context
import com.snaggly.ksw_toolkit.util.adb.AdbManager

class AdbConnection {
    private val adbShellListeners = ArrayList<ShellObserver>()
    private val adbManager = AdbManager()
    var hasShutKsw = false

    fun connect(context: Context) {
        adbManager.connect(context, "shell:", object : AdbManager.OnAdbShellDataReceived {
            override fun onDataReceived(text: String) {
                for (listener in adbShellListeners)
                    listener.update(text)
            }
        })
    }

    fun disconnect() {
        adbManager.disconnect()
    }

    fun sendCommand(command: String) {
        adbManager.sendCommand(command)
    }

    fun stopKsw() {
        sendCommand("am stopservice --user 0 com.wits.pms/com.wits.pms.mcu.McuService")
        hasShutKsw = true
    }

    fun startKsw() {
        sendCommand("am startservice --user 0 com.wits.pms/com.wits.pms.mcu.McuService")
        hasShutKsw = false
    }

    fun registerShellListener(listener: ShellObserver) {
        adbShellListeners.add(listener)
    }

    fun unregisterShellListener(listener: ShellObserver) {
        adbShellListeners.remove(listener)
    }
}