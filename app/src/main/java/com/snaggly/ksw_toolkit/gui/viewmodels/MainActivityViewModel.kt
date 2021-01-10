package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.snaggly.ksw_toolkit.core.service.McuService
import com.snaggly.ksw_toolkit.core.service.helper.McuServiceConnector

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var mcuServiceIntent: Intent? = null
    private var context: Context = getApplication<Application>().applicationContext
    private var connector = McuServiceConnector(context)

    fun startService() {
        mcuServiceIntent = Intent(context, McuService::class.java)
        context.startForegroundService(mcuServiceIntent)
    }

    fun stopService() {
        context.stopService(mcuServiceIntent)
    }

    fun bindToMcuService() {
        connector.connectToService()
    }

    fun unbindFromMcuService() {
        connector.disconnectFromService()
    }
}