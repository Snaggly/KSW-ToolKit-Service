package com.snaggly.ksw_toolkit.core.service

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.service.adb.AdbServiceConnection
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.McuReaderHandler
import com.snaggly.ksw_toolkit.core.service.mcu.PreCheckLogcat
import com.snaggly.ksw_toolkit.core.service.remote.KSWToolKitService
import com.snaggly.ksw_toolkit.core.service.remote.ServiceValidation
import com.snaggly.ksw_toolkit.receiver.ZLinkReceiver
import com.wits.pms.bean.ZlinkMessage
import kotlin.random.Random

class CoreService : Service() {
    init {
        Thread.setDefaultUncaughtExceptionHandler { _, error ->
            Log.e("KSW-ToolKit-Service", "Uncaught Exception found: '${error.message}'")
            error.printStackTrace()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        try {
            ServiceValidation.signature = intent.getByteArrayExtra("Authentication")
        } catch (e: Exception) {
        }
        ServiceValidation.hasAuthenticated = false
        return kswToolKitService
    }

    override fun onRebind(intent: Intent?) {
        if (intent != null) {
            try {
                ServiceValidation.signature = intent.getByteArrayExtra("Authentication")
            } catch (e: Exception) {
            }
        } else
            ServiceValidation.signature = null
        ServiceValidation.hasAuthenticated = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        ServiceValidation.signature = null
        ServiceValidation.hasAuthenticated = false
        return super.onUnbind(intent)
    }

    private var kswToolKitService: KSWToolKitService? = null
    private var mcuReaderHandler: McuReaderHandler? = null
    private val zLinkReceiver = ZLinkReceiver()

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        PreCheckLogcat().getLightsStatus()
        registerReceiver(
            zLinkReceiver,
            IntentFilter(ZlinkMessage.ZLINK_NORMAL_ACTION)
        )
        try {
            val lMcuReaderHandler = McuReaderHandler(applicationContext, zLinkReceiver)
            lMcuReaderHandler.startMcuReader()
            mcuReaderHandler = lMcuReaderHandler
            kswToolKitService = KSWToolKitService(lMcuReaderHandler)
            lMcuReaderHandler.showStartMessage()
        } catch (e: Exception) {
            e.printStackTrace()
            crashOut("Could not start McuReader!\n\n${e.message}")
        }
    }

    override fun onDestroy() {
        unregisterReceiver(zLinkReceiver)
        kswToolKitService = null
        showAlertMessage(this, "KSW-ToolKit-Service stopped")
        AdbServiceConnection.startKsw(applicationContext)
        mcuReaderHandler?.stopReader()
        super.onDestroy()
    }

    private fun String.showMessage() {
        Toast.makeText(this@CoreService, this, Toast.LENGTH_SHORT).show()
    }

    private fun startTest() {
        val lMcuReaderHandler = McuReaderHandler(applicationContext, zLinkReceiver)
        mcuReaderHandler = lMcuReaderHandler
        kswToolKitService = KSWToolKitService(lMcuReaderHandler)
        McuLogic.mcuCommunicator = null

        Thread {
            while (kswToolKitService != null) {
                lMcuReaderHandler.onMcuEventAction.update(
                    Random.nextInt(128),
                    Random.nextBytes(Random.nextInt(1, 22))
                )
                Thread.sleep(1500)
            }
        }.start()
    }

    private fun crashOut(message: String) {
        showAlertMessage(this, message)
        stopSelf()
    }

    companion object {
        fun showAlertMessage(context: Context, message: String) {
            val alert =
                AlertDialog.Builder(context, R.style.alertDialogNight)
                    .setTitle("KSW-ToolKit-CoreService")
                    .setMessage(message)
                    .create()
            alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            alert.show()
        }
    }
}
