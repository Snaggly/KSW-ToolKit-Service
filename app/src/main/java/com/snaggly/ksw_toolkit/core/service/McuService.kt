package com.snaggly.ksw_toolkit.core.service

import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import com.snaggly.ksw_toolkit.util.adb.AdbManager
import projekt.auto.mcu.adb.lib.AdbStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class McuService : Service() {
    inner class McuServiceBinder : Binder() {
        fun getService(): McuService = this@McuService
    }

    private val binder = McuServiceBinder()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    interface ShellObserver {
        fun update(text: String)
    }

    private val adbManager = AdbManager()
    private val adbShellListeners = ArrayList<ShellObserver>()
    private lateinit var adbShell : AdbStream

    override fun onCreate() {
        super.onCreate()
        try {
            adbShell = adbManager.connect(applicationContext, "shell:", object : AdbManager.OnAdbShellDataReceived {
                override fun onDataReceived(text: String) {
                    for (listener in adbShellListeners)
                        listener.update(text)
                }
            })
        }
        catch (e : Exception) {
            Log.d("KSW-ToolKit-McuService", e.localizedMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adbManager.disconnect()
    }

    fun registerShellListener(listener: ShellObserver) {
        adbShellListeners.add(listener)
    }

    fun unregisterShellListener(listener: ShellObserver) {
        adbShellListeners.remove(listener)
    }
}
