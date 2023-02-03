package com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces

import android.content.Context
import com.snaggly.ksw_toolkit.core.service.mcu.parser.LightEvent
import com.snaggly.ksw_toolkit.util.brightnesstools.AdvancedBrightnessHandler
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

abstract class ICarDataEvent {
    var lightEvent : ILightEvent = LightEvent()
    abstract fun getCarDataEvent(data: ByteArray) : EventManagerTypes
}