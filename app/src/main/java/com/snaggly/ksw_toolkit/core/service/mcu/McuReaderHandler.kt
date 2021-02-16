package com.snaggly.ksw_toolkit.core.service.mcu

import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbServiceConnection
import com.snaggly.ksw_toolkit.core.service.sys_observers.BrightnessObserver
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.commander.AppStarter
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode
import com.snaggly.ksw_toolkit.util.commander.KeyInjector
import com.snaggly.ksw_toolkit.util.commander.McuCommander
import com.snaggly.ksw_toolkit.util.list.mcu.McuCommandsEnum
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.reader.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.reader.SerialReader

class McuReaderHandler(private val context: Context) {
    private val mcuEventListeners = ArrayList<McuEventObserver>()
    private val config = ConfigManager.getConfig(context.filesDir.absolutePath)
    private val brightnessObserver = BrightnessObserver(context)
    private val sendingInterceptor = McuSenderInterceptor()
    private var hasSerialInit = false

    init {
        McuLogic.mcuCommunicator = McuCommunicator.getInstance()
    }

    private val initialSerialStartAction = McuCommunicator.McuAction { cmdType, data ->
        if (!hasSerialInit) {
            if (cmdType == 0x1C && data[0] == 0x1.toByte()) {
                hasSerialInit = true
                McuLogic.mcuCommunicator!!.mcuReader.stopReading()
                AdbServiceConnection.stopKsw()

                McuLogic.mcuCommunicator!!.mcuReader = SerialReader()
                McuLogic.mcuCommunicator!!.mcuReader.startReading(onMcuEventAction)

                if (config.systemTweaks.interceptMcuCommand.data) {
                    sendingInterceptor.startReading(fun(cmdType: Int, data: ByteArray) {
                        McuLogic.mcuCommunicator?.sendCommand(cmdType, data, false)
                    })
                } else {
                    McuLogic.mcuCommunicator!!.startBeat()
                    brightnessObserver.startObservingBrightness()
                }

                if (config.systemTweaks.carDataLogging.data)
                    McuLogic.isLogging = true

                McuLogic.backTapper = BackTapper(context)
            }
        }
    }

    private val onMcuEventAction = McuCommunicator.McuAction { cmdType, data ->
        Thread {
            val event = McuLogic.getMcuEvent(cmdType, data)
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
                        McuCommander.executeCommand(McuCommandsEnum.values[eventConfig.mcuCommandMode.data], McuLogic.mcuCommunicator, context)
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
        }.start()
    }

    fun startMcuReader() {
        McuLogic.hasNoOEMScreen = PowerManagerApp.getSettingsInt("CarDisplay") == 0
        AdbServiceConnection.startKsw()
        McuLogic.mcuCommunicator!!.mcuReader = LogcatReader()
        if (config.systemTweaks.kswService.data) {
            McuLogic.mcuCommunicator!!.mcuReader.startReading(onMcuEventAction)
        } else {
            McuLogic.mcuCommunicator!!.mcuReader.startReading(initialSerialStartAction)
        }

        if (config.systemTweaks.autoVolume.data) {
            McuLogic.startAutoVolume(context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        }
    }

    fun stopReader() {
        brightnessObserver.stopObservingBrightness()
        McuLogic.backTapper = null
        McuLogic.stopAutoVolume()
        McuLogic.isLogging = false
        McuLogic.mcuCommunicator?.stopBeat()
        sendingInterceptor.stopReading()
        McuLogic.mcuCommunicator?.mcuReader?.stopReading()
        hasSerialInit = false
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