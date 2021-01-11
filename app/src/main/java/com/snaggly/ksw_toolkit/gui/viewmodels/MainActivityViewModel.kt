package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.snaggly.ksw_toolkit.core.service.CoreService
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceConnector

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var mcuServiceIntent: Intent? = null
    private var context: Context = getApplication<Application>().applicationContext
    private var connector = CoreServiceConnector(context)

    fun startService() {
        mcuServiceIntent = Intent(context, CoreService::class.java)
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