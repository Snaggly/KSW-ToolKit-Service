package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class IdleEvent(private val backTapper: BackTapper) {
    private var ticks = 0
    private var wasInSys2 = false

    fun getIdleEvent(data: ByteArray) : EventManagerTypes {
        McuLogic.setRealSysMode(data[0].toInt(), backTapper)
        if (data[0] == 0x1.toByte()) {
            if (wasInSys2) {
                if (ticks >= 1) {
                    McuLogic.actionLock = false
                    wasInSys2 = false
                    ticks = 0
                } else {
                    ticks++
                }
            }
        } else if (data[0] == 0x2.toByte()) {
            McuLogic.actionLock = true
            wasInSys2 = true
        }
        return EventManagerTypes.Idle
    }
}