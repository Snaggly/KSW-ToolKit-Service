package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class SystemStatusEvent(private val backTapper: BackTapper) {
    fun parseSystemStatusEvent(data: ByteArray) : EventManagerTypes {
        McuLogic.setRealSysMode(data[0].toInt(), backTapper)
        return EventManagerTypes.SystemStatusEvent
    }
}