package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.util.Log
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus

class MediaStringEvent {
    fun getMediaEvent(data: ByteArray) : EventManagerTypes {
        try {
            if (McuLogic.mcuStat.mediaData.parseMediaStringInfoEvent(data, McuLogic.mcuStat.mediaStringInfo))
                WitsStatus.sendOutMcuMedia(McuLogic.mcuStat.mediaData)
        } catch (e : Exception) {
            Log.e("MediaStringEvent", "Error in handling MediaStringEvent\n${e.message}")
        }
        return EventManagerTypes.MediaDataEvent
    }
}