package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.enums.SOUND_SRC_TYPE

object ScreenSwitchEventNoOEMScreen : IScreenSwitchEvent {
    override fun getScreenSwitch(data: ByteArray): EventManagerTypes {
        if (data[1] == 0x1.toByte()) {
            McuLogic.mcuCommunicator!!.sendCommand(McuCommands.SYS_SCREEN_ON)
            if (thisHasSoundRestorer) {
                McuLogic.mcuCommunicator!!.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_ATSL_AIRCONSOLE))
            }
        } else if (data[1] == 0x2.toByte()) {
            McuLogic.mcuCommunicator!!.sendCommand(McuCommands.SYS_SCREEN_OFF)
        }

        return EventManagerTypes.ScreenSwitch
    }

    override var hasSoundRestorer: Boolean
        get() = thisHasSoundRestorer
        set(value) {thisHasSoundRestorer = value}

    private var thisHasSoundRestorer : Boolean = false
}