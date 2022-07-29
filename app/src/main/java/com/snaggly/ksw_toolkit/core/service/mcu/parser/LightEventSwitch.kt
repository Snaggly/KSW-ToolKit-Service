package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.app.UiModeManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.ILightEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import kotlin.experimental.and

object LightEventSwitch : ILightEvent {

    var uiModeManager : UiModeManager? = null

    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        if (data[1].and(7) > 1) {
            uiModeManager?.nightMode = UiModeManager.MODE_NIGHT_YES
            if(McuLogic.nightBrightness >= 0) {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(McuLogic.nightBrightness.toByte()))
            }
        } else {
            uiModeManager?.nightMode = UiModeManager.MODE_NIGHT_NO
            if(McuLogic.nightBrightness >= 0) {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(100.toByte()))
            }
        }
        return EventManagerTypes.CarData
    }
}