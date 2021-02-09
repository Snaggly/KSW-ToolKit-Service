package com.snaggly.ksw_toolkit.core.service.mcu

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbConnection
import com.snaggly.ksw_toolkit.core.service.sys_observers.NaviAppObserver
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.applist.AppStarter
import com.snaggly.ksw_toolkit.util.enums.EventMode
import com.snaggly.ksw_toolkit.util.keyevent.KeyInjector
import com.snaggly.ksw_toolkit.util.mcu.McuCommander
import com.snaggly.ksw_toolkit.util.mcu.McuCommandsEnum
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.reader.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.reader.SerialReader

class McuReaderHandler(val context: Context, private val adb : AdbConnection, private val eventLogic: McuEventLogicImpl) {
    private val mcuEventListeners = ArrayList<McuEventObserver>()
    private val config = ConfigManager.getConfig(context.filesDir.absolutePath)
    val naviObserver = NaviAppObserver(context, eventLogic)

    init {
        eventLogic.mcuCommunicator = McuCommunicator.getInstance()
    }

    private val initialSerialStartAction = McuCommunicator.McuAction { cmdType, data ->
        if (cmdType == 0x1C && data[0] == 0x1.toByte()) {
            eventLogic.mcuCommunicator!!.mcuReader.stopReading()
            adb.stopKsw()
            eventLogic.mcuCommunicator!!.mcuReader = SerialReader()
            eventLogic.mcuCommunicator!!.startBeat()
            eventLogic.mcuCommunicator!!.mcuReader.startReading(onMcuEventAction)
            if (config.systemTweaks.carDataLogging.data)
                eventLogic.startSendingCarData()
            eventLogic.backTapper = BackTapper(context)
            if (config.systemTweaks.muxNaviVoice.data)
                naviObserver.startHandlingNaviCallouts()
        }
    }

    private val onMcuEventAction = McuCommunicator.McuAction { cmdType, data ->
        val event = eventLogic.getMcuEvent(cmdType, data)
        if (event != null) {
            val eventConfig = config.eventManagers[event]
            when (eventConfig?.eventMode) {
                EventMode.KeyEvent -> {
                    KeyInjector.sendKey(eventConfig.keyCode.data)
                }
                EventMode.StartApp -> {
                    AppStarter.launchAppById(eventConfig.appName.data, context)
                }
                EventMode.McuCommand -> {
                    McuCommander.executeCommand(McuCommandsEnum.values[eventConfig.mcuCommandMode.data], eventLogic.mcuCommunicator, context)
                }
                else -> {}
            }
            if (config.systemTweaks.logMcuEvent.data) {
                val cmdTypeString = String.format("%02X", cmdType)
                var dataString = ""
                for (i in 0 .. data.size-2) {
                    dataString += String.format("%02X", data[i])
                    dataString += "-"
                }
                dataString += String.format("%02X", data.last())
                Log.i("KswMcuListener", "--Mcu toString-----[ cmdType:$cmdTypeString - data:$dataString ]")
            }
        }

        for (mcuEventListener in mcuEventListeners)
            mcuEventListener.update(event, cmdType, data)
    }

    fun startMcuReader() {
        eventLogic.hasNoOEMScreen = PowerManagerApp.getSettingsInt("CarDisplay") == 0
        adb.startKsw()
        eventLogic.mcuCommunicator!!.mcuReader = LogcatReader()
        if (config.systemTweaks.kswService.data) {
            eventLogic.mcuCommunicator!!.mcuReader.startReading(onMcuEventAction)
        } else {
            eventLogic.mcuCommunicator!!.mcuReader.startReading(initialSerialStartAction)
        }

        if (config.systemTweaks.autoVolume.data) {
            eventLogic.startAutoVolume(context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        }
    }

    fun stopReader() {
        naviObserver.stopHandlingNaviCallouts()
        eventLogic.backTapper = null
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