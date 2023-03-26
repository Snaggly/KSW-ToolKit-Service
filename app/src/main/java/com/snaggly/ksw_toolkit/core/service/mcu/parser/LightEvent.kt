package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.brightnesstools.AdvancedBrightnessHandler
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import kotlin.experimental.and


class LightEvent {
    var advancedBrightnessHandler : AdvancedBrightnessHandler? = null
    var autoThemeManager : ((darkMode : Boolean) -> Unit)? = null

    fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        if (data[1].and(7) >= 1) {
            McuLogic.isAnyLightOn = true
            autoThemeManager?.invoke(true)
        } else {
            McuLogic.isAnyLightOn = false
            autoThemeManager?.invoke(false)
        }
        advancedBrightnessHandler?.trigger()
        return EventManagerTypes.CarData
    }
}