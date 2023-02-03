package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.app.UiModeManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.ILightEvent
import com.snaggly.ksw_toolkit.util.brightnesstools.AdvancedBrightnessHandler
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import kotlin.experimental.and

class LightEventSwitch : ILightEvent() {

    var uiModeManager: UiModeManager? = null

    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        if (data[1].and(7) >= 1) {
            uiModeManager?.nightMode = UiModeManager.MODE_NIGHT_YES
            McuLogic.isAnyLightOn = true
        } else {
            uiModeManager?.nightMode = UiModeManager.MODE_NIGHT_NO
            McuLogic.isAnyLightOn = false
        }
        advancedBrightnessHandler.trigger()
        return EventManagerTypes.CarData
    }
}