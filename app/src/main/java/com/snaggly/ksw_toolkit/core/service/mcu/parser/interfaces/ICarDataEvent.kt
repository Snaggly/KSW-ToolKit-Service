package com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces

import android.content.Context
import com.snaggly.ksw_toolkit.core.service.mcu.parser.LightEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

abstract class ICarDataEvent {
    companion object var lightEvent : ILightEvent = LightEvent
    abstract fun getCarDataEvent(data: ByteArray, context: Context) : EventManagerTypes
}