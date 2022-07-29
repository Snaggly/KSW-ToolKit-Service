package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.IBenzDataEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object BenzDataEventLogger : IBenzDataEvent {
    override fun getBenzDataEvent(data: ByteArray): EventManagerTypes {
        if (data.size <= 5)
            return EventManagerTypes.Dummy
        McuLogic.mcuStat.benzData.parseFromBenzDataEvent(data)
        McuLogic.sendCarData()

        return EventManagerTypes.BenzData
    }
}