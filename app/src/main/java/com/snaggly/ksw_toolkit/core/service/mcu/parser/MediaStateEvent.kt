package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus

class MediaStateEvent {
    fun getMediaEvent(data: ByteArray) : EventManagerTypes {
        if (data.size <= 2)
            return EventManagerTypes.Dummy
        McuLogic.mcuStat.mediaPlayStatus.parseStatusEvent(data, McuLogic.mcuStat.mediaData)
        WitsStatus.sendOutMcuMedia(McuLogic.mcuStat.mediaData)
        WitsStatus.sendOutMcuMediaPlayStatus(McuLogic.mcuStat.mediaPlayStatus)
        return EventManagerTypes.MediaDataEvent
    }
}