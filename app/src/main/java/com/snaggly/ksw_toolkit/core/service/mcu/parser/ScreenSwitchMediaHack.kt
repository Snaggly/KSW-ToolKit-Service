package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.enums.SOUND_SRC_TYPE

object ScreenSwitchMediaHack : IScreenSwitchEvent {
    private val enableOEM = byteArrayOf(0x0e, 0x01)
    private val disableOEM = byteArrayOf(0x0e, 0x00)
    private var switchLock = false

    override fun getScreenSwitch(data: ByteArray): EventManagerTypes {
        if (data[1] == 0x1.toByte()) {
            switchLock = false
            McuLogic.mcuCommunicator!!.sendCommand(0x70, disableOEM, false)
            if (thisHasSoundRestorer)
                McuLogic.mcuCommunicator!!.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_ATSL_AIRCONSOLE))
        } else if (data[1] == 0x2.toByte() && !switchLock) {
            switchLock = true
            McuLogic.mcuCommunicator!!.sendCommand(0x70, enableOEM, false)
            McuLogic.mcuCommunicator!!.sendCommand(McuCommands.SWITCH_TO_OEM)
        }

        return EventManagerTypes.ScreenSwitch
    }

    override var hasSoundRestorer: Boolean
        get() = thisHasSoundRestorer
        set(value) {thisHasSoundRestorer = value}

    private var thisHasSoundRestorer : Boolean = false
}