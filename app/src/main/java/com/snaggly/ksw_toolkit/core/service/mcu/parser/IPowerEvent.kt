package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

interface IPowerEvent {
    fun getPowerEvent(data: ByteArray) : EventManagerTypes
}