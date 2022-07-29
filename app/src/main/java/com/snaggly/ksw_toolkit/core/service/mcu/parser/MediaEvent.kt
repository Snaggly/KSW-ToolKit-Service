package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus

class MediaEvent {
    fun getMediaEvent(data: ByteArray) : EventManagerTypes {
        McuLogic.mcuStat.mediaData.parseFromMediaEvent(data)

        when(McuLogic.mcuStat.mediaData.type) {
            1 -> {
                if (data.size > 3)
                    McuLogic.mcuStat.mediaData.fm.parseFMFromEvent(data)
                else
                    return EventManagerTypes.Dummy
            }
            21 -> {
                if (data.size <= 6)
                    return EventManagerTypes.Dummy
                McuLogic.mcuStat.bluetoothStatus.parseFromMediaEvent(data)
                WitsStatus.sendOutMcuBTStatus(McuLogic.mcuStat.bluetoothStatus)
            }
            64 -> {
                if (data.size > 6)
                    McuLogic.mcuStat.mediaData.mode.parseFromMediaEvent(data)
                else
                    return EventManagerTypes.Dummy
            }
            16 -> {
                if (data.size > 6)
                    McuLogic.mcuStat.mediaData.disc.parseFromMediaEvent(data, McuLogic.mcuStat.mediaStringInfo)
                else
                    return EventManagerTypes.Dummy
            }
            17 -> {
                if (data.size > 6)
                    McuLogic.mcuStat.mediaData.usb.parseFromMediaEvent(data, McuLogic.mcuStat.mediaStringInfo)
                else
                    return EventManagerTypes.Dummy
            }
            18 -> {
                if (data.size > 6)
                    McuLogic.mcuStat.mediaData.usb.parseFromMediaEvent(data, McuLogic.mcuStat.mediaStringInfo)
                else
                    return EventManagerTypes.Dummy
            }
        }
        WitsStatus.sendOutMcuMedia(McuLogic.mcuStat.mediaData)
        return EventManagerTypes.MediaDataEvent
    }
}