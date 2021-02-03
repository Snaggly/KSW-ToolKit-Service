package com.snaggly.ksw_toolkit.core.service.mcu

import android.content.Context
import android.media.AudioManager
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbConnection
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.applist.AppStarter
import com.snaggly.ksw_toolkit.util.enums.EventMode
import com.snaggly.ksw_toolkit.util.keyevent.KeyInjector
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.reader.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.reader.SerialReader
import projekt.auto.mcu.ksw.serial.writer.SerialWriter

class McuReaderHandler(val context: Context, private val adb : AdbConnection, private val eventLogic: McuEventLogicImpl) {
    private val mcuEventListeners = ArrayList<McuEventObserver>()
    private val config = ConfigManager.getConfig(context.filesDir.absolutePath)

    private val onMcuEventAction = McuCommunicator.McuAction { cmdType, data ->
        val event = eventLogic.getMcuEvent(cmdType, data)
        if (event != null) {
            val eventConfig = config.eventManagers[event]
            if (eventConfig?.eventMode == EventMode.KeyEvent) {
                KeyInjector.sendKey(eventConfig.keyCode.data)
            }
            else if (eventConfig?.eventMode == EventMode.StartApp) {
                AppStarter.launchAppById(eventConfig.appName.data, context)
            }
        }

        for (mcuEventListener in mcuEventListeners)
            mcuEventListener.update(event, cmdType, data)
    }

    fun startMcuReader() {
        eventLogic.hasNoOEMScreen = PowerManagerApp.getSettingsInt("CarDisplay") == 0
        if (config.systemTweaks.kswService.data) {
            adb.startKsw()
            eventLogic.backTapper = null
            eventLogic.stopSendingCarData()
            eventLogic.mcuCommunicator = McuCommunicator(SerialWriter(), LogcatReader())
            eventLogic.mcuCommunicator!!.mcuReader.startReading(onMcuEventAction)
        } else {
            adb.stopKsw()
            eventLogic.backTapper = BackTapper(context)
            if (config.systemTweaks.carDataLogging.data)
                eventLogic.startSendingCarData()
            eventLogic.mcuCommunicator = McuCommunicator(SerialWriter(), SerialReader())
            eventLogic.mcuCommunicator!!.startBeat()
            eventLogic.mcuCommunicator!!.mcuReader!!.startReading(onMcuEventAction)
        }

        if (config.systemTweaks.autoVolume.data) {
            eventLogic.startAutoVolume(context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        }
    }

    fun stopReader() {
        eventLogic.stopAutoVolume()
        eventLogic.stopSendingCarData()
        eventLogic.mcuCommunicator?.stopBeat()
        eventLogic.mcuCommunicator?.mcuReader?.stopReading()
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