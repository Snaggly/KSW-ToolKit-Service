package com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces

import com.snaggly.ksw_toolkit.core.service.mcu.parser.LightEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

abstract class ICarDataEvent {
    var lightEvent = LightEvent()
    abstract fun getCarDataEvent(data: ByteArray) : EventManagerTypes
}