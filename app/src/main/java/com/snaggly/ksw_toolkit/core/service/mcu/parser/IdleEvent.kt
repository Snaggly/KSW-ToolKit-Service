package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class IdleEvent(val context: Context) {
    private var backTapper: BackTapper? = null
    private var ticks = 0
    private var wasInSys2 = false

    fun armBackTapper() {
        backTapper = BackTapper(context)
    }

    fun clearBackTapper() {
        backTapper?.removeBackWindow()
        backTapper = null
    }

    fun getIdleEvent(data: ByteArray) : EventManagerTypes {
        McuLogic.realSysMode = data[0].toInt()
        if (data[0] == 0x1.toByte()) {
            backTapper?.removeBackWindow()
            if (wasInSys2) {
                if (ticks >= 1) {
                    McuLogic.actionLock = false
                    wasInSys2 = false
                    ticks = 0
                } else {
                    ticks++
                }
            }
        } else {
            backTapper?.drawBackWindow(McuLogic.mcuCommunicator!!)
            McuLogic.actionLock = true
            wasInSys2 = true
        }
        return EventManagerTypes.Idle
    }
}