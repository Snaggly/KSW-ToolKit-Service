package com.snaggly.ksw_toolkit.util.brightnesstools

import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class TimeBasedBrightness(private val context: Context) : AdvancedBrightnessHandler() {

    fun setBrightness(context: Context, newDaytime : DaytimeObserver.Daytime) {
        val config = ConfigManager.getConfig(context)
        val newBrightness : Int =
            if (McuLogic.isAnyLightOn) {
                when (newDaytime) {
                    DaytimeObserver.Daytime.Day -> config.advancedBrightness.daylightHLBrightness!!
                    DaytimeObserver.Daytime.Night, DaytimeObserver.Daytime.Morning -> config.advancedBrightness.nightHLBrightnessLevel!!
                    else -> return
                }
            } else {
                when(newDaytime) {
                    DaytimeObserver.Daytime.Day -> config.advancedBrightness.daylightBrightness!!
                    DaytimeObserver.Daytime.Night, DaytimeObserver.Daytime.Morning -> config.advancedBrightness.nightBrightnessLevel!!
                    else -> return
                }
            }
        PowerManagerApp.setSettingsInt("Brightness", newBrightness)
        McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(newBrightness.toByte()))
    }

    override fun trigger() {
        setBrightness(context, DaytimeObserver.CurrentDaytime)
    }

    override fun destroy() {
        //daytimeObserver.removeDaytimeListener(daytimeListener)
    }

}