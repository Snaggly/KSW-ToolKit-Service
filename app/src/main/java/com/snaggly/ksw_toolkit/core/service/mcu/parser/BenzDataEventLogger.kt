package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.IBenzDataEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus

object BenzDataEventLogger : IBenzDataEvent {
    override fun getBenzDataEvent(data: ByteArray): EventManagerTypes {
        if (data.size <= 5)
            return EventManagerTypes.Dummy
        McuLogic.mcuStat.benzData.parseFromBenzDataEvent(data)
        WitsStatus.sendOutMcuStatus(McuLogic.mcuStat)

        return EventManagerTypes.BenzData
    }
}