package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object PowerEvent : IPowerEvent {
    override fun getPowerEvent(data: ByteArray): EventManagerTypes {
        return EventManagerTypes.Dummy
    }
}