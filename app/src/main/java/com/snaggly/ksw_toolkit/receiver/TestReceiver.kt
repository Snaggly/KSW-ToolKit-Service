package com.snaggly.ksw_toolkit.receiver

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.snaggly.ksw_toolkit.BuildConfig
import com.snaggly.ksw_toolkit.core.service.CoreService
import com.snaggly.ksw_toolkit.core.service.remote.KSWToolKitService
import com.wits.pms.handler.ZLinkHandler

class TestReceiver : BroadcastReceiver() {
    private var context: Context? = null
    private var intent: Intent? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service == null || service !is KSWToolKitService)
                return

            when (intent?.action) {
                "mcu_btn" -> {
                    val btnEventCode = intent?.getIntExtra("code", -1)
                    if (btnEventCode != null) {
                        if (btnEventCode >= 0)
                            service.coreReaderHandler.testBtnEvent(btnEventCode.toByte())
                    }
                }
                "hl_on" -> {
                    Log.d("snaggle", "Test HL on with: ${service.coreReaderHandler}")
                    service.coreReaderHandler.triggerTestHLOn()
                }
                "hl_off" -> {
                    Log.d("snaggle", "Test HL off with: ${service.coreReaderHandler}")
                    service.coreReaderHandler.triggerTestHLOff()
                }
                "zlink_code" -> {
                    val requestCode = intent?.getIntExtra("code", -1)
                    if (requestCode != null) {
                        if (requestCode >= 0)
                            ZLinkHandler(context).testCommand(requestCode)
                    }
                }
                "zlink_out_stop" -> ZLinkHandler(context).setLightTheme()
                "zlink_out_start" -> ZLinkHandler(context).setDarkTheme()
                "power_off" -> service.coreReaderHandler.testPowerOff()
                "power_on" -> service.coreReaderHandler.testPowerOn()
            }
            context?.unbindService(this)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("snaggle", "Service disconnected from TestReceiver")
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!BuildConfig.DEBUG)
            return
        if (intent == null || context == null)
            return

        this.intent = intent
        this.context = context.applicationContext

        this.context?.bindService(
            Intent(context, CoreService::class.java),
            serviceConnection,
            Context.BIND_EXTERNAL_SERVICE
        )
    }
}