package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import projekt.auto.mcu.ksw.serial.collection.McuCommands

object ScreenSwitchEvent : IScreenSwitchEvent {
    override fun getScreenSwitch(data: ByteArray): EventManagerTypes {
        return EventManagerTypes.ScreenSwitch
    }
}