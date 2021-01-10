package com.snaggly.ksw_toolkit.core.service.helper

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.snaggly.ksw_toolkit.core.service.McuService

class McuServiceConnector(private val context: Context) {

    private val serviceConnector = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            McuServiceClient.mcuService = (service as McuService.McuServiceBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            McuServiceClient.mcuService = null
        }
    }

    fun connectToService() {
        Intent(context, McuService::class.java).also { intent ->
            context.bindService(intent, serviceConnector, Context.BIND_AUTO_CREATE)
        }
    }

    fun disconnectFromService() {
        context.unbindService(serviceConnector)
    }
}