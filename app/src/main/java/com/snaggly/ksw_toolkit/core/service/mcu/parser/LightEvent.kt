package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.ILightEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import kotlin.experimental.and

object LightEvent : ILightEvent {
    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        if (McuLogic.nightBrightness >= 0) {
            if (data[1].and(7) > 1) {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(McuLogic.nightBrightness.toByte()))
            } else {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(100.toByte()))
            }
        }

        return EventManagerTypes.CarData
    }
}