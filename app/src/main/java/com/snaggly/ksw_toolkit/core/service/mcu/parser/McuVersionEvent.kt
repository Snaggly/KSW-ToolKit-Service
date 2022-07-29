package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.utils.McuVersionParser

object McuVersionEvent {
    fun getMcuVersionEvent(data: ByteArray) : EventManagerTypes {
        if (data.size < 40)
            return EventManagerTypes.Dummy

        McuVersionParser.parseMcuVersion(McuLogic.mcuStat, data)
        return EventManagerTypes.McuVersionEvent
    }
}