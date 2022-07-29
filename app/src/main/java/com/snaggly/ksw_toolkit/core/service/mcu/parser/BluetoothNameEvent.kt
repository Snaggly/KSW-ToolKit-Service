package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus

class BluetoothNameEvent {
    fun getNameEvent(data: ByteArray) : EventManagerTypes {
        McuLogic.mcuStat.bluetoothStatus.parseFromInfoEvent(data, McuLogic.mcuStat.mediaData)
        WitsStatus.sendOutMcuBTStatus(McuLogic.mcuStat.bluetoothStatus)
        WitsStatus.sendOutMcuMedia(McuLogic.mcuStat.mediaData)
        return EventManagerTypes.BluetoothStatusEvent
    }
}