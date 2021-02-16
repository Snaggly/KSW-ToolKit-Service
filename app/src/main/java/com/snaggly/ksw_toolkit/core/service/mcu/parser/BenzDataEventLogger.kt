package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object BenzDataEventLogger : IBenzDataEvent {
    override fun getBenzDataEvent(data: ByteArray): EventManagerTypes {
        McuLogic.mcuStat.benzData.parseFromBenzDataEvent(data)
        McuLogic.sendCarData()

        return EventManagerTypes.BenzData
    }
}