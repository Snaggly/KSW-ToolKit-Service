package com.snaggly.ksw_toolkit.core.service.mcu

import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.util.McuSourceAdapter
import projekt.auto.mcu.ksw.serial.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.SerialReader

class McuReader(context: Context) {
    private var mcuReader: McuCommunicator.Reader? = null
    private val mcuEventListeners = ArrayList<McuEventObserver>()
    private val eventLogic = McuEventLogicImpl()
    private val config = ConfigManager.getConfig(context.filesDir.absolutePath).mcuListener
    private val adapter = McuSourceAdapter(context)

    private val onMcuEventAction = McuCommunicator.McuAction { cmdType, data ->
        for (mcuEventListener in mcuEventListeners)
            mcuEventListener.update(eventLogic.getMcuEvent(cmdType, data), cmdType, data)
    }

    fun startMcuReader() {
        val currentSource = config.mcuSource.data
        if (currentSource == 0) {
            mcuReader = LogcatReader()
            (mcuReader as LogcatReader).startReading(onMcuEventAction)
        } else {
            mcuReader = SerialReader()
            (mcuReader as SerialReader).startReading(onMcuEventAction, adapter.getSourceString(currentSource))
        }
    }

    fun stopReader() {
        mcuReader?.stopReading()
    }

    fun restartReader() {
        stopReader()
        startMcuReader()
    }

    fun registerMcuEventListener(listener: McuEventObserver) {
        mcuEventListeners.add(listener)
    }

    fun unregisterMcuEventListener(listener: McuEventObserver) {
        mcuEventListeners.remove(listener)
    }
}