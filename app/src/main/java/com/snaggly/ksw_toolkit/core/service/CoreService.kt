package com.snaggly.ksw_toolkit.core.service

import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import android.widget.Toast
import com.snaggly.ksw_toolkit.R
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbServiceConnection
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.McuReaderHandler
import com.snaggly.ksw_toolkit.core.service.remote.KSWToolKitService
import com.snaggly.ksw_toolkit.core.service.remote.ServiceValidation
import kotlin.random.Random

class CoreService : Service() {
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

    val mcuLogic = McuLogic
    private var mcuReaderHandler: McuReaderHandler? = null
    private var kswToolKitService: KSWToolKitService? = null

    override fun onCreate() {
        try {
            mcuReaderHandler = McuReaderHandler(applicationContext)
            mcuReaderHandler!!.startMcuReader()
            kswToolKitService = KSWToolKitService(this, mcuReaderHandler!!)
            mcuReaderHandler!!.showStartMessage()
        } catch (e: Exception) {
            crashOut("Could not start McuReader!\n\n${e.stackTrace}")
        }
        //startTest()
    }

    override fun onDestroy() {
        kswToolKitService = null
        showAlertMessage("KSW-ToolKit-Service stopped")
        AdbServiceConnection.startKsw(applicationContext)
        mcuReaderHandler?.stopReader()
        super.onDestroy()
    }

    private fun String.showMessage() {
        Toast.makeText(this@CoreService, this, Toast.LENGTH_SHORT).show()
    }

    private fun startTest() {
        mcuReaderHandler = McuReaderHandler(applicationContext)
        kswToolKitService = KSWToolKitService(this, mcuReaderHandler!!)
        McuLogic.mcuCommunicator = null

        Thread {
            while (kswToolKitService != null) {
                mcuReaderHandler?.onMcuEventAction?.update(Random.nextInt(128), Random.nextBytes(Random.nextInt(1, 22)))
                Thread.sleep(1500)
            }
        }.start()
    }

    private fun showAlertMessage(message: String) {
        val alert =
            AlertDialog.Builder(this, R.style.alertDialogNight).setTitle("KSW-ToolKit-CoreService").setMessage(message)
                .create()
        alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        alert.show()
    }

    private fun crashOut(message: String) {
        showAlertMessage(message)
        stopSelf()
    }
}
