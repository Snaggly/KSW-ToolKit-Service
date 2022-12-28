package com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces

import android.content.Context
import com.snaggly.ksw_toolkit.core.service.mcu.parser.LightEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

abstract class ICarDataEvent(private var context: Context) {
    companion object var lightEvent : ILightEvent = LightEvent(context)
    abstract fun getCarDataEvent(data: ByteArray) : EventManagerTypes
}