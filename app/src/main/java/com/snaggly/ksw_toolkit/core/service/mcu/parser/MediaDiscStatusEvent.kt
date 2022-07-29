package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus

class MediaDiscStatusEvent {
    fun getMediaEvent(data: ByteArray) : EventManagerTypes {
        if (data.size <= 2)
            return EventManagerTypes.Dummy
        McuLogic.mcuStat.discStatus.parseFromStatusEvent(data)
        WitsStatus.sendOutMcuDiscStatus(McuLogic.mcuStat.discStatus)
        return EventManagerTypes.MediaDataEvent
    }
}