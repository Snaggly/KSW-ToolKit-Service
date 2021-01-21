package com.snaggly.ksw_toolkit.core.service.mcu

import projekt.auto.mcu.ksw.serial.McuCommunicator

interface McuReaderTemplate : McuCommunicator.McuAction {
    override fun update(cmdType: Int, data: ByteArray?)
}