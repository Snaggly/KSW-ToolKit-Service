package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.Application
import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.SystemTweaks
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceClient
import com.snaggly.ksw_toolkit.util.adb.AdbManager

class SystemTwaksViewModel(application: Application) : CoreServiceClient(application) {

    private var config: SystemTweaks? = null

    fun getConfig(context: Context) : SystemTweaks {
        if (config == null)
            config = ConfigManager.getConfig(context.filesDir.absolutePath).systemTweaks
        return config!!
    }

    fun restartMcuReader() {
        coreService?.mcuReaderHandler?.restartReader()
    }

    fun shrinkTopBar() {
        AdbManager.sendCommand("wm overscan 0,-9,0,0", getApplication<Application>().applicationContext)
    }

    fun restoreTopBar() {
        AdbManager.sendCommand("wm overscan 0,0,0,0", getApplication<Application>().applicationContext)
    }

    fun showTopBar() {
        AdbManager.sendCommand("settings put global policy_control null*", getApplication<Application>().applicationContext)
    }

    fun hideTopBar() {
        AdbManager.sendCommand("settings put global policy_control immersive.full=*", getApplication<Application>().applicationContext)
    }

    fun giveTaskerPerm() {
        AdbManager.sendCommand("pm grant net.dinglisch.android.taskerm android.permission.READ_LOGS", getApplication<Application>().applicationContext)
    }
}