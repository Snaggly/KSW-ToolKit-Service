package com.snaggly.ksw_toolkit.core.service

import projekt.auto.mcu.ksw.serial.McuCommunicator

class CoreReaderTemplate : McuCommunicator.McuAction {
    override fun update(cmdType: Int, data: ByteArray?) {
        TODO("Not yet implemented")
    }
}