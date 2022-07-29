package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import android.os.Handler
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus
import com.wits.pms.utils.AccLight
import com.wits.pms.utils.SystemProperties

class AccEvent(val context: Context) {
    fun getAccEvent(data : ByteArray) : EventManagerTypes {
        val isAccOpen = data[0] == 1.toByte()
        if (isAccOpen) {
            WitsStatus.setAcc(McuLogic.systemStatus, 1)
            AccLight.show(500L)
        } else {
            Handler(context.mainLooper).postDelayed( { SystemProperties.set("sys.powerctl", "shutdown") }, 1000L)
        }
        return EventManagerTypes.AccEvent
    }
}