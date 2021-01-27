package com.snaggly.ksw_toolkit.gui.viewmodels

import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceClient

class SystemTwaksViewModel : CoreServiceClient() {

    var config: ConfigManager? = null

    fun initConfig(context: Context) {
        config = ConfigManager.getConfig(context.filesDir.absolutePath)
    }

    fun restartMcuReader(isKswChecked : Boolean, context: Context) {
        config?.mcuListener!!.enableKsw.data = isKswChecked
        try {
            coreService?.mcuReaderHandler?.restartReader()
        } catch (exception: Exception) {
            val alert = AlertDialog.Builder(context).setTitle("KSW-ToolKit-McuListener")
                    .setMessage("Could not restart McuReader!\n\n${exception.localizedMessage}").create()
            alert.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            alert.show()
        }
    }
}