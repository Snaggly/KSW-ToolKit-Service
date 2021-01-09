package com.snaggly.ksw_toolkit.core.service

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.snaggly.ksw_toolkit.BuildConfig
import com.snaggly.ksw_toolkit.util.adb.AdbManager
import projekt.auto.mcu.ksw.serial.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.McuEvent
import java.util.*
import kotlin.collections.ArrayList


class McuService : Service() {
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val notificationChannelId = BuildConfig.APPLICATION_ID
        val channelName = "McuListenerService"
        val chan = NotificationChannel(notificationChannelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLACK
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        val notification: Notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.sym_def_app_icon)
                .setContentTitle("KSW-Toolkit running in background...")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        startForeground(2, notification)
    }

    inner class McuServiceBinder : Binder() {
        fun getService(): McuService = this@McuService
    }

    private val binder = McuServiceBinder()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    interface ShellObserver {
        fun update()
    }

    interface McuEventObserver {
        fun update(eventType: McuEvent?, cmdType: Int, data: ByteArray)
    }

    private val mcuEventListeners = ArrayList<McuEventObserver>()
    private val eventLogic = McuEventLogicImpl()
    private val adbManager = AdbManager()
    private val adbShellListeners = ArrayList<ShellObserver>()
    val adbLines = ArrayList<String>()
    private var counter = 0;

    private var mcuReader: McuCommunicator.Reader? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMyOwnForeground()
        adbLines.add("")
        var atLine = 0
        try {
            adbManager.connect(applicationContext, "shell:", object : AdbManager.OnAdbShellDataReceived {
                override fun onDataReceived(text: String) {
                    adbLines[atLine] += text
                    Log.d("Snaggly", "McuService adbShell OnDataReceived - Text: $text - ListenersSize: ${adbShellListeners.size} - Counter: ${counter++}")
                    for (listener in adbShellListeners)
                        listener.update()
                }
            })
        } catch (e: Exception) {
            Log.d("KSW-ToolKit-McuService", e.localizedMessage!!)
            var alert = AlertDialog.Builder(this).setTitle("KSW-ToolKit-McuService").setMessage("Could not connect to Adb!\n\n${e.localizedMessage}").create()
            alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            alert.show()
            //stopSelf()
            return START_NOT_STICKY
        }
        checkPermission()
        startMcuReader()
        Log.d("Snaggly", "Started McuService")
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        mcuReader?.stopReading()
        adbManager.disconnect()
        Log.d("Snaggly", "Stopped McuService")
    }


    private fun checkPermission() {
        if (applicationContext.checkSelfPermission("android.permission.READ_LOGS") != PackageManager.PERMISSION_GRANTED) {
            sendAdbCommand("pm grant ${BuildConfig.APPLICATION_ID} android.permission.READ_LOGS")
            var alert = AlertDialog.Builder(this).setTitle("KSW-ToolKit-McuService").setMessage("Granted READ_LOGS permission.\nPlease restart the app for effects to take in place").create()
            alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            alert.show()
        }
    }

    private fun startMcuReader() {
        mcuReader = LogcatReader()
        LogcatReader().startReading { cmdType, data ->
            for (mcuEventListener in mcuEventListeners)
                mcuEventListener.update(eventLogic.getMcuEvent(cmdType, data), cmdType, data)
        }
        Log.d("Snaggly", "Started McuServiceReader")
    }

    fun sendAdbCommand(command: String) {
        adbManager.sendCommand(command)
    }

    fun registerShellListener(listener: ShellObserver) {
        adbShellListeners.add(listener)
    }

    fun unregisterShellListener(listener: ShellObserver) {
        adbShellListeners.remove(listener)
    }

    fun registerMcuEventListener(listener: McuEventObserver) {
        mcuEventListeners.add(listener)
    }

    fun unregisterMcuEventListener(listener: McuEventObserver) {
        mcuEventListeners.remove(listener)
    }
}
