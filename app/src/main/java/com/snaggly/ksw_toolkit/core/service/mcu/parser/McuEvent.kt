package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

class McuEvent(context: Context) : IMcuEvent(context) {
    override fun getMcuEvent(cmdType: Int, data: ByteArray): EventManagerTypes? {
        when (cmdType) {
            0xA1 -> {
                if (data[0] == 0x17.toByte() && data.size > 2) {
                    return buttonClickEvent.getClickEvent(data)
                } else if (data[0] == 0x1A.toByte() && data.size > 1) {
                    return screenSwitchEvent.getScreenSwitch(data)
                } else if (data.size > 1) {
                    return carDataEvent.getCarDataEvent(data)
                }
                return EventManagerTypes.CarData
            }
            0x1D -> return benzDataEvent.getBenzDataEvent(data)
            0x1C -> return idleEvent.getIdleEvent(data)
            else -> return null
        }
    }
}