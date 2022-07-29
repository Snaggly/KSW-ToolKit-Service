package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.utils.TimeSetting

class TimeEvent(val context: Context) {
    fun getTimeEvent(data: ByteArray): EventManagerTypes {
        TimeSetting.processTimeEvent(data, context)
        return EventManagerTypes.Time
    }
}