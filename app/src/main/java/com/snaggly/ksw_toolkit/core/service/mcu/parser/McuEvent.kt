package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class McuEvent(screenSwitchEvent: IScreenSwitchEvent, carDataEvent: ICarDataEvent, benzDataEvent: IBenzDataEvent) : IMcuEvent(screenSwitchEvent, carDataEvent, benzDataEvent) {
    override fun getMcuEvent(cmdType: Int, data: ByteArray): EventManagerTypes? {
        when (cmdType) {
            0xA1 -> {
                if (data[0] == 0x17.toByte() && data.size > 2) {
                    return buttonClickEvent.getClickEvent(data)
                } else if (data[0] == 0x1A.toByte() && data.size > 1) {
                    return screenSwitchEvent.getScreenSwitch(data)
                }
                return carDataEvent.getCarDataEvent(data)
            }
            0x1D -> return benzDataEvent.getBenzDataEvent(data)
            0x1C -> return idleEvent.getIdleEvent(data)
            else -> return null
        }
    }
}