package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object ScreenSwitchEvent : IScreenSwitchEvent {
    override fun getScreenSwitch(data: ByteArray): EventManagerTypes {
        return EventManagerTypes.ScreenSwitch
    }
}