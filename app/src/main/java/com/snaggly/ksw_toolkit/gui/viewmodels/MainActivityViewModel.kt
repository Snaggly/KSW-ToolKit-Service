package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.snaggly.ksw_toolkit.core.service.CoreService
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceConnector
import com.snaggly.ksw_toolkit.util.adb.AdbManager

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    fun startService() {
        val mcuServiceIntent = Intent(getApplication<Application>().applicationContext, CoreService::class.java)
        getApplication<Application>().applicationContext.startForegroundService(mcuServiceIntent)
    }

    fun stopService() {
        val mcuServiceIntent = Intent(getApplication<Application>().applicationContext, CoreService::class.java)
        getApplication<Application>().applicationContext.stopService(mcuServiceIntent)
    }

    fun bindToMcuService() {
        CoreServiceConnector(getApplication<Application>().applicationContext).connectToService()
    }

    fun unbindFromMcuService() {
        CoreServiceConnector(getApplication<Application>().applicationContext).disconnectFromService()
    }
    fun stopKsw() {
        AdbManager.sendCommand("am stopservice --user 0 com.wits.pms/com.wits.pms.mcu.McuService\nappops set com.wits.pms SYSTEM_ALERT_WINDOW deny", getApplication<Application>().applicationContext)
    }

    fun startKsw() {
        AdbManager.sendCommand("am startservice --user 0 com.wits.pms/com.wits.pms.mcu.McuService\nappops set com.wits.pms SYSTEM_ALERT_WINDOW allow", getApplication<Application>().applicationContext)
    }
}