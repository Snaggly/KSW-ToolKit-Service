package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class IdleEvent(val context: Context) {
    private var backTapper: BackTapper? = null

    fun armBackTapper() {
        backTapper = BackTapper(context)
    }

    fun clearBackTapper() {
        backTapper?.removeBackWindow()
        backTapper = null
    }

    fun getIdleEvent(data: ByteArray) : EventManagerTypes {
        if (data[0] == 0x1.toByte()) {
            backTapper?.removeBackWindow()
        } else {
            backTapper?.drawBackWindow(McuLogic.mcuCommunicator!!)
        }
        return EventManagerTypes.Idle
    }
}