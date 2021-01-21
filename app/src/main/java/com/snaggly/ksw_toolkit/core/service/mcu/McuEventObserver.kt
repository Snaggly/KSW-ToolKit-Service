package com.snaggly.ksw_toolkit.core.service.mcu

import projekt.auto.mcu.ksw.serial.McuEvent

interface McuEventObserver {
    fun update(eventType: McuEvent?, cmdType: Int, data: ByteArray)
}