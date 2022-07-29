package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.IScreenSwitchEvent
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class ScreenSwitchEvent(private val backTapper: BackTapper) : IScreenSwitchEvent() {
    override fun getScreenSwitch(data: ByteArray): EventManagerTypes {

        McuLogic.setRealSysMode(data[1].toInt(), backTapper)
        if (McuLogic.realSysMode == 0x1)
            super.processToAndroid()
        else if (McuLogic.realSysMode == 0x2)
            super.processToOEM()

        return EventManagerTypes.ScreenSwitch
    }
}