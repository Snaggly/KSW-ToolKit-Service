package com.snaggly.ksw_toolkit.core.service

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.snaggly.ksw_toolkit.BuildConfig
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.adb.AdbConnection
import com.snaggly.ksw_toolkit.core.service.mcu.McuReader
import java.util.*
import kotlin.system.exitProcess


class CoreService : Service() {
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
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("KSW-Toolkit running in background...")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        startForeground(2, notification)
    }

    inner class McuServiceBinder : Binder() {
        fun getService(): CoreService = this@CoreService
    }

    private val binder = McuServiceBinder()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    val adbConnection = AdbConnection()
    var mcuReader : McuReader? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMyOwnForeground()
        try {
            adbConnection.connect(applicationContext)
        } catch (e: Exception) {
            return crashOut("Could not connect to Adb!\n\n${e.localizedMessage}")
        }
        checkPermission()
        try {
            mcuReader = McuReader(applicationContext, adbConnection)
            mcuReader!!.startMcuReader()
        } catch (e: Exception) {
            return crashOut("Could not start McuReader!\n\n${e.localizedMessage}")
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (adbConnection.hasShutKsw)
            adbConnection.startKsw()
        mcuReader?.stopReader()
        adbConnection.disconnect()
    }

    private fun checkPermission() {
        if (applicationContext.checkSelfPermission("android.permission.READ_LOGS") != PackageManager.PERMISSION_GRANTED) {
            adbConnection.sendCommand("pm grant ${BuildConfig.APPLICATION_ID} android.permission.READ_LOGS")
            val alert = AlertDialog.Builder(this).setTitle("KSW-ToolKit-McuService").setMessage("Granted READ_LOGS permission.\nPlease restart the app for effects to take in place").create()
            alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Close") { _, _ -> exitProcess(0) }
            alert.show()
        }
    }

    private fun crashOut(message: String) : Int {
        val alert = AlertDialog.Builder(this).setTitle("KSW-ToolKit-CoreService").setMessage(message).create()
        alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Close") { _, _ -> exitProcess(0) }
        alert.show()
        stopSelf()
        return START_NOT_STICKY
    }
}
