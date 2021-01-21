package com.snaggly.ksw_toolkit.core.service.mcu

import projekt.auto.mcu.ksw.serial.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator

class McuReader {
    private var mcuReader: McuCommunicator.Reader? = null
    private val mcuEventListeners = ArrayList<McuEventObserver>()
    private val eventLogic = McuEventLogicImpl()

    fun startMcuReader() {
        mcuReader = LogcatReader()
        LogcatReader().startReading { cmdType, data ->
            for (mcuEventListener in mcuEventListeners)
                mcuEventListener.update(eventLogic.getMcuEvent(cmdType, data), cmdType, data)
        }
    }

    fun stopReader() {
        mcuReader?.stopReading()
    }

    fun registerMcuEventListener(listener: McuEventObserver) {
        mcuEventListeners.add(listener)
    }

    fun unregisterMcuEventListener(listener: McuEventObserver) {
        mcuEventListeners.remove(listener)
    }
}