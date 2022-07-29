package com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

interface ILightEvent {
    fun getCarDataEvent(data: ByteArray) : EventManagerTypes
}