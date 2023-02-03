package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.ILightEvent
import com.snaggly.ksw_toolkit.util.brightnesstools.AdvancedBrightnessHandler
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import kotlin.experimental.and


class LightEvent() : ILightEvent() {

    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        McuLogic.isAnyLightOn = data[1].and(7) >= 1
        advancedBrightnessHandler.trigger()

        return EventManagerTypes.CarData
    }
}