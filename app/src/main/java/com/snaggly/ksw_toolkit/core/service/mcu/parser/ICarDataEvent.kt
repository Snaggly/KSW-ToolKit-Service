package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

abstract class ICarDataEvent {
    companion object var lightEvent : ILightEvent = LightEvent
    abstract fun getCarDataEvent(data: ByteArray) : EventManagerTypes
}