package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object LightEvent : ILightEvent {
    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        return EventManagerTypes.CarData
    }
}