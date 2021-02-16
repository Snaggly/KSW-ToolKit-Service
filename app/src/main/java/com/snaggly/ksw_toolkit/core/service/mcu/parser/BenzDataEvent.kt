package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object BenzDataEvent : IBenzDataEvent {
    override fun getBenzDataEvent(data: ByteArray): EventManagerTypes {
        return EventManagerTypes.BenzData
    }
}