package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object IdleEvent {
    fun getIdleEvent(data: ByteArray) : EventManagerTypes {
        if (data[0] == 0x1.toByte()) {
            McuLogic.backTapper?.removeBackWindow()
        } else {
            McuLogic.backTapper?.drawBackWindow(McuLogic.mcuCommunicator!!)
        }
        return EventManagerTypes.Idle
    }
}