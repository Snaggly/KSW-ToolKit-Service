package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.app.UiModeManager
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.ILightEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import kotlin.experimental.and

object LightEventSwitch : ILightEvent {

    var uiModeManager : UiModeManager? = null

    override fun getCarDataEvent(data: ByteArray, context: Context): EventManagerTypes {
        if (data[1].and(7) > 1) {
            uiModeManager?.nightMode = UiModeManager.MODE_NIGHT_YES
            if(McuLogic.nightBrightness >= 0) {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(McuLogic.nightBrightness.toByte()))
                Log.d("KSWToolKit", "N 2:" + McuLogic.nightBrightness.toString())
            }
        } else {
            uiModeManager?.nightMode = UiModeManager.MODE_NIGHT_NO
            if(McuLogic.nightBrightness >= 0) {
                //McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(100.toByte()))
                val nowBrightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel((nowBrightness * 100 / 255).toByte()))
                Log.d("KSWToolKit", "D 2:" + (nowBrightness * 100 / 255).toString() + " / " + Settings.System.SCREEN_BRIGHTNESS)
            }
        }
        return EventManagerTypes.CarData
    }
}