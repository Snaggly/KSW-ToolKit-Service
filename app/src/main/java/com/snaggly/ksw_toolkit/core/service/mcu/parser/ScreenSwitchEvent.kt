package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class ScreenSwitchEvent(private val backTapper: BackTapper) {
    fun getScreenSwitch(data: ByteArray) : EventManagerTypes {
        if (data.size < 2)
            return EventManagerTypes.Dummy

        McuLogic.setRealSysMode(data[1].toInt(), backTapper)

        return EventManagerTypes.ScreenSwitch
    }
}