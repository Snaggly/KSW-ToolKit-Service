package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import android.os.Handler
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus
import java.io.FileNotFoundException

class EQDataEvent(context: Context) {
    private val replyData = byteArrayOf(0)
    private val handler = Handler(context.mainLooper)

    fun getEQDataEvent(data: ByteArray) : EventManagerTypes {
        if (data.size <= 1)
            return EventManagerTypes.Dummy
        McuLogic.mcuStat.eqData.parseFromEqEvent(data, McuLogic.mcuStat.mediaPlayStatus, McuLogic.mcuStat.mediaData)
        WitsStatus.sendOutMcuEqData(McuLogic.mcuStat.eqData)
        handler.postDelayed({
            try {
                McuLogic.mcuCommunicator?.sendCommand(0x7B, replyData, false)
            }
            catch (fnfe: FileNotFoundException) {}
        }, 3500L)
        return EventManagerTypes.EQDataEvent
    }
}