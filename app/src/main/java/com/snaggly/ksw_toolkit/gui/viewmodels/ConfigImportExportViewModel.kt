package com.snaggly.ksw_toolkit.gui.viewmodels

import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceClient

class ConfigImportExportViewModel : CoreServiceClient() {
    fun importConfig(context: Context) : String {
        val storagePath = "${context.getExternalFilesDir(null)?.absolutePath}/KSW-Config.dat"
        storagePath.let { ConfigManager.importConfig(context.filesDir.absolutePath, it) }
        return storagePath
    }

    fun exportConfig(context: Context) : String {
        val storagePath = "${context.getExternalFilesDir(null)?.absolutePath}/KSW-Config.dat"
        storagePath.let { ConfigManager.exportConfig(it) }
        return storagePath
    }

    fun restartSystem() {
        coreService?.adbConnection!!.sendCommand("reboot")
    }
}