package com.snaggly.ksw_toolkit.core.service.adb

import android.content.Context
import android.util.Log
import com.snaggly.ksw_toolkit.util.adb.AdbManager

class AdbConnection {
    private val adbShellListeners = ArrayList<ShellObserver>()
    private val adbManager = AdbManager()
    val adbLines : ArrayList<String> = arrayListOf("")

    fun connect(context: Context) {
        adbManager.connect(context, "shell:", object : AdbManager.OnAdbShellDataReceived {
            override fun onDataReceived(text: String) {
                adbLines[0] += text
                Log.d("Snaggly", "McuService adbShell OnDataReceived - Text: $text - ListenersSize: ${adbShellListeners.size}")
                for (listener in adbShellListeners)
                    listener.update()
            }
        })
    }

    fun disconnect() {
        adbManager.disconnect()
    }

    fun sendCommand(command: String) {
        adbManager.sendCommand(command)
    }

    fun registerShellListener(listener: ShellObserver) {
        adbShellListeners.add(listener)
    }

    fun unregisterShellListener(listener: ShellObserver) {
        adbShellListeners.remove(listener)
    }
}