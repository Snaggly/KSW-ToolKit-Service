package com.snaggly.ksw_toolkit.core.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.snaggly.ksw_toolkit.BuildConfig
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.adb.AdbServiceConnection
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.McuReaderHandler

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

    companion object var thisInstance : CoreService? = null

    inner class McuServiceBinder : Binder() {
        fun getService(): CoreService {
            if (thisInstance == null)
                thisInstance = this@CoreService
            return thisInstance!!
        }
    }

    private val binder = McuServiceBinder()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    val mcuLogic = McuLogic
    var mcuReaderHandler : McuReaderHandler? = null

    override fun onCreate() {
        startMyOwnForeground()

        try {
            mcuReaderHandler = McuReaderHandler(applicationContext)
            mcuReaderHandler!!.startMcuReader()
        } catch (e: Exception) {
            crashOut("Could not start McuReader!\n\n${e.stackTrace}")
        }

        showMessage("KSW-ToolKit-Service started")
    }

    override fun onDestroy() {
        showAlertMessage("KSW-ToolKit-Service stopped")
        AdbServiceConnection.startKsw(applicationContext)
        mcuReaderHandler?.stopReader()
        super.onDestroy()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAlertMessage(message: String) {
        val alert = AlertDialog.Builder(this, R.style.alertDialogNight).setTitle("KSW-ToolKit-CoreService").setMessage(message).create()
        alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        alert.show()
    }

    private fun crashOut(message: String) {
        showAlertMessage(message)
        stopSelf()
    }
}
