package com.snaggly.ksw_toolkit.gui.viewmodels

import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.SystemTweaks
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceClient

class SystemTwaksViewModel : CoreServiceClient() {

    private var config: SystemTweaks? = null

    fun getConfig(context: Context) : SystemTweaks {
        if (config == null)
            config = ConfigManager.getConfig(context.filesDir.absolutePath).systemTweaks
        return config!!
    }

    fun restartMcuReader() {
        coreService?.mcuReaderHandler?.restartReader()
    }

    fun changeDPI(dpi: Int) {
        coreService?.adbConnection!!.sendCommand("wm density $dpi")
    }

    fun shrinkTopBar() {
        coreService?.adbConnection!!.sendCommand("wm overscan 0,-9,0,0\nwm density ${config?.dpi!!.data-2}")
    }

    fun restoreTopBar() {
        coreService?.adbConnection!!.sendCommand("wm overscan 0,0,0,0\nwm density ${config?.dpi!!.data}")
    }

    fun showTopBar() {
        coreService?.adbConnection!!.sendCommand("settings put global policy_control null*")
    }

    fun hideTopBar() {
        coreService?.adbConnection!!.sendCommand("settings put global policy_control immersive.full=*")
    }
}