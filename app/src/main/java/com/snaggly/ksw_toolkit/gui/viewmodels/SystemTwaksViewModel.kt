package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceClient

class SystemTwaksViewModel : CoreServiceClient() {

    var config: ConfigManager? = null

    fun initConfig(context: Context) {
        if (config == null)
            config = ConfigManager.getConfig(context.filesDir.absolutePath)
    }

    fun restartMcuReader() {
        coreService?.mcuReaderHandler?.restartReader()
    }

    fun changeDPI(dpi: Int) {
        coreService?.adbConnection?.sendCommand("wm density $dpi")
    }

    fun shrinkTopBar() {
        coreService?.adbConnection?.sendCommand("wm overscan 0,-9,0,0\nwm density ${config?.systemTweaks?.dpi!!.data-2}")
    }

    fun restoreTopBar() {
        coreService?.adbConnection?.sendCommand("wm overscan 0,0,0,0\nwm density ${config?.systemTweaks?.dpi!!.data+2}")
    }

    fun showTopBar() {
        coreService?.adbConnection?.sendCommand("settings put global policy_control null*")
    }

    fun hideTopBar() {
        coreService?.adbConnection?.sendCommand("settings put global policy_control immersive.full=*")
    }
}