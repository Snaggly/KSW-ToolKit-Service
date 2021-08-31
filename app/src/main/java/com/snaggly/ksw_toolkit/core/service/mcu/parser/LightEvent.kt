package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import kotlin.experimental.and

object LightEvent : ILightEvent {
    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        if (thisHasNightBrightness) {
            if (data[1].and(7) == 0x05.toByte()) {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(thisNightBrightnessLevel.toByte()))
            } else {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(100.toByte()))
            }
        }

        return EventManagerTypes.CarData
    }

    override var hasNightBrightness: Boolean
        get() = thisHasNightBrightness
        set(value) {thisHasNightBrightness = value}
    override var nightBrightnessLevel: Int
        get() = thisNightBrightnessLevel
        set(value) {thisNightBrightnessLevel = value}

    private var thisHasNightBrightness : Boolean = false
    private var thisNightBrightnessLevel : Int = 0
}