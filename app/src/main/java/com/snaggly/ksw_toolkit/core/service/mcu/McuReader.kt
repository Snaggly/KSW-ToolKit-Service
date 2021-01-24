package com.snaggly.ksw_toolkit.core.service.mcu

import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbConnection
import com.snaggly.ksw_toolkit.util.adapters.McuSourceAdapter
import com.snaggly.ksw_toolkit.util.enums.EventMode
import projekt.auto.mcu.ksw.serial.reader.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.reader.SerialReader
import projekt.auto.mcu.ksw.serial.writer.SerialWriter

class McuReader(val context: Context, private val adb : AdbConnection) {
    private var communicator : McuCommunicator? = null
    private val mcuEventListeners = ArrayList<McuEventObserver>()
    private val eventLogic = McuEventLogicImpl()
    private val config = ConfigManager.getConfig(context.filesDir.absolutePath)

    private val onMcuEventAction = McuCommunicator.McuAction { cmdType, data ->
        val event = eventLogic.getMcuEvent(cmdType, data)
        val eventConfig = config.eventManagers[event]
        if (eventConfig != null) {
            if (eventConfig.eventMode == EventMode.KeyEvent) {
                adb.sendKeyEvent(eventConfig.keyCode.data)
            }
            else if (eventConfig.eventMode == EventMode.StartApp) {
                adb.startApp(eventConfig.appName.data)
            }
        }
        for (mcuEventListener in mcuEventListeners)
            mcuEventListener.update(event, cmdType, data)
    }

    fun startMcuReader() {
        val currentSource = config.mcuListener.mcuSource.data
        val mcuSource = McuSourceAdapter(context).getSourceString(currentSource)
        if (currentSource == 0) {
            adb.startKsw()
            communicator = McuCommunicator(SerialWriter(), LogcatReader())
            communicator!!.mcuReader.startReading(onMcuEventAction)
        } else {
            adb.stopKsw()
            communicator = McuCommunicator(SerialWriter(mcuSource), SerialReader(mcuSource))
            communicator!!.startBeat()
            communicator!!.mcuReader!!.startReading(onMcuEventAction)
        }
    }

    fun stopReader() {
        communicator?.stopBeat()
        communicator?.mcuReader?.stopReading()
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