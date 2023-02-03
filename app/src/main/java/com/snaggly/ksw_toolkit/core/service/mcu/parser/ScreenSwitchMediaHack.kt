package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.IScreenSwitchEvent
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class ScreenSwitchMediaHack(val backTapper: BackTapper) : IScreenSwitchEvent() {
    private val enableOEM = byteArrayOf(0x0e, 0x01)
    private val disableOEM = byteArrayOf(0x0e, 0x00)
    private var switchLock = false @Synchronized get

    override fun getScreenSwitch(data: ByteArray): EventManagerTypes {

        McuLogic.setRealSysMode(data[1].toInt(), backTapper)
        if (McuLogic.realSysMode == 0x1) {
            switchLock = false
            McuLogic.mcuCommunicator?.sendCommand(0x70, disableOEM, false)
            super.processToAndroid()
        } else if (McuLogic.realSysMode == 0x2 && !switchLock) {
            switchLock = true
            McuLogic.mcuCommunicator?.sendCommand(0x70, enableOEM, false)
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_OEM)
            super.processToOEM()
        }

        return EventManagerTypes.ScreenSwitch
    }

    override fun restoreState() {
        McuLogic.mcuCommunicator?.sendCommand(0x70, enableOEM, false)
        if (McuLogic.realSysMode == 2)
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_OEM)
        else
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_ANDROID)
    }
}