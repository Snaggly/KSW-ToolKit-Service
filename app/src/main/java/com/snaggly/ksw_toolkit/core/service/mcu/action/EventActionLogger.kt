package com.snaggly.ksw_toolkit.core.service.mcu.action

import android.content.Context
import android.util.Log
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class EventActionLogger(context: Context) : EventAction(context) {
    override fun processAction(cmdType: Int, data: ByteArray, event: EventManagerTypes?, config: ConfigManager) {
        super.processAction(cmdType, data, event, config)

        val cmdTypeString = String.format("%02X", cmdType)
        var dataString = ""
        for (i in 0..data.size - 2) {
            dataString += String.format("%02X", data[i])
            dataString += "-"
        }
        dataString += String.format("%02X", data.last())
        Log.i("KswMcuListener", "--Mcu toString-----[ cmdType:$cmdTypeString - data:$dataString ]")
    }
}