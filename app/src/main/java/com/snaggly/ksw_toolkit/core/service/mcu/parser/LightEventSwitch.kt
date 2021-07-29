package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.app.UiModeManager
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import kotlin.experimental.and

object LightEventSwitch : ILightEvent {

    var uiModeManager : UiModeManager? = null

    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {
        if (data[1].and(7) == 0x05.toByte()) {
            uiModeManager?.nightMode = UiModeManager.MODE_NIGHT_YES
        } else {
            uiModeManager?.nightMode = UiModeManager.MODE_NIGHT_NO
        }
        return EventManagerTypes.CarData
    }

}