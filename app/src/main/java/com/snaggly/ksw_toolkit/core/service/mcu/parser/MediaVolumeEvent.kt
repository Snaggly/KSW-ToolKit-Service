package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus

class MediaVolumeEvent {
    fun handleNewVolume(data: ByteArray) : EventManagerTypes {
        WitsStatus.setMcuVolumeLevel(McuLogic.mcuStat.mediaData.getVolumeFromEvent(data))
        WitsStatus.setMcuVolumeMute(McuLogic.mcuStat.mediaData.getMuteFromEvent(data))
        return EventManagerTypes.MediaDataEvent
    }
}