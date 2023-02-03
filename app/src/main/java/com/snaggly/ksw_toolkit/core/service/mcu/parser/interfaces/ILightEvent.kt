package com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces

import com.snaggly.ksw_toolkit.util.brightnesstools.AdvancedBrightnessHandler
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

abstract class ILightEvent {
    abstract fun getCarDataEvent(data: ByteArray) : EventManagerTypes
    var advancedBrightnessHandler = AdvancedBrightnessHandler.getHandler()
}