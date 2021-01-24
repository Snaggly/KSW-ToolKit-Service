package com.snaggly.ksw_toolkit.core.service.mcu

import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes

interface McuEventObserver {
    fun update(eventType: EventManagerTypes?, cmdType: Int, data: ByteArray)
}