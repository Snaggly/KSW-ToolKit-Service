package com.snaggly.ksw_toolkit.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.widget.Toast
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbServiceConnection
import com.snaggly.ksw_toolkit.util.adb.DensityUtil
import com.snaggly.ksw_toolkit.util.manager.AndroidVolumeManager
import com.wits.pms.statuscontrol.PowerManagerApp

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val config = ConfigManager.getConfig(context)

            if (config.systemOptions.retainVolume!!) {
                try {
                    AndroidVolumeManager(context.applicationContext).restoreSavedVolumes()
                } catch (exc: AndroidVolumeManager.WitsConnectionException) {
                    Toast.makeText(context, "Unable to connect to Wits! ${exc.message}", Toast.LENGTH_LONG).show()
                } catch (exc: Exception) {
                    Toast.makeText(context, "Error in restoring volumes! ${exc.message}", Toast.LENGTH_LONG).show()
                }
            }

            if (config.systemOptions.tabletMode!!) {
                DensityUtil.turnOnTabletMode(context)
            }

            if (config.systemOptions.startAtBoot!!) {
                AdbServiceConnection.startThisService(context)
            }
        }
    }
}