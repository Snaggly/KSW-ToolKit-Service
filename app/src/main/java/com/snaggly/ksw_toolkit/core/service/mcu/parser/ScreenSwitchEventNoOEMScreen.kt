package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuCommands

object ScreenSwitchEventNoOEMScreen : IScreenSwitchEvent {
    override fun getScreenSwitch(data: ByteArray): EventManagerTypes {
        if (data[1] == 0x1.toByte()) {
            McuLogic.mcuCommunicator!!.sendCommand(McuCommands.SYS_SCREEN_ON)
        } else if (data[1] == 0x2.toByte()) {
            McuLogic.mcuCommunicator!!.sendCommand(McuCommands.SYS_SCREEN_OFF)
        }

        return EventManagerTypes.ScreenSwitch
    }
}