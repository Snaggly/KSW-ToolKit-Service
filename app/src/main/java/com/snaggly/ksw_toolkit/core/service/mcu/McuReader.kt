package com.snaggly.ksw_toolkit.core.service.mcu

import android.content.Context
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbConnection
import com.snaggly.ksw_toolkit.util.adapters.McuSourceAdapter
import com.snaggly.ksw_toolkit.util.applist.AppStarter
import com.snaggly.ksw_toolkit.util.enums.EventMode
import com.snaggly.ksw_toolkit.util.keyevent.KeyInjector
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
                KeyInjector.sendKey(eventConfig.keyCode.data)
            }
            else if (eventConfig.eventMode == EventMode.StartApp) {
                AppStarter.launchAppById(eventConfig.appName.data, context)
            }
        }
        for (mcuEventListener in mcuEventListeners)
            mcuEventListener.update(event, cmdType, data)
    }

    fun startMcuReader() {
        if (config.mcuListener.enableKsw.data) {
            adb.startKsw()
            communicator = McuCommunicator(SerialWriter(), LogcatReader())
            communicator!!.mcuReader.startReading(onMcuEventAction)
        } else {
            adb.stopKsw()
            communicator = McuCommunicator(SerialWriter(), SerialReader())
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