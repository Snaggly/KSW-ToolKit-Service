package com.snaggly.ksw_toolkit.core.service.mcu

import android.app.UiModeManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbServiceConnection
import com.snaggly.ksw_toolkit.core.service.mcu.action.EventAction
import com.snaggly.ksw_toolkit.core.service.mcu.action.EventActionLogger
import com.snaggly.ksw_toolkit.core.service.mcu.parser.*
import com.snaggly.ksw_toolkit.core.service.sys_observers.BrightnessObserver
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.reader.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.reader.SerialReader

class McuReaderHandler(private val context: Context) {
    private var mcuEventListener : McuEventObserver? = null
    private val config = ConfigManager.getConfig(context.filesDir.absolutePath)
    private val brightnessObserver = BrightnessObserver(context)
    private val sendingInterceptor = McuSenderInterceptor()
    private lateinit var eventAction : EventAction
    private var parseMcuEvent = McuEvent(context)
    private var hasSerialInit = false

    init {
        McuLogic.mcuCommunicator = McuCommunicator.getInstance()
    }

    private val initialSerialStartAction = McuCommunicator.McuAction { cmdType, data ->
        if (!hasSerialInit) {
            if (cmdType == 0x1C && data[0] == 0x1.toByte()) {
                hasSerialInit = true
                McuLogic.mcuCommunicator!!.mcuReader.stopReading()
                AdbServiceConnection.stopKsw(context)

                if (config.systemTweaks.logMcuEvent.data)
                    eventAction = EventActionLogger(context)

                if (config.systemTweaks.carDataLogging.data) {
                    parseMcuEvent.carDataEvent = CarDataEventLogger
                    parseMcuEvent.benzDataEvent = BenzDataEventLogger
                }

                parseMcuEvent.carDataEvent.lightEvent = if (config.systemTweaks.autoTheme.data) {
                    LightEventSwitch.apply {
                        uiModeManager = context.getSystemService(UiModeManager::class.java)
                    }
                } else {
                    LightEvent
                }

                when {
                    Build.VERSION.RELEASE.contains("11") -> {
                        McuLogic.mcuCommunicator!!.mcuReader = SerialReader("/dev/ttyHS1")
                    }
                    Build.DISPLAY.contains("8937") -> {
                        McuLogic.mcuCommunicator!!.mcuReader = SerialReader("/dev/ttyHSL1")
                    }
                    else -> {
                        McuLogic.mcuCommunicator!!.mcuReader = SerialReader()
                    }
                }

                McuLogic.mcuCommunicator!!.mcuReader.startReading(onMcuEventAction)

                if (config.systemTweaks.interceptMcuCommand.data) {
                    sendingInterceptor.startReading(fun(cmdType: Int, data: ByteArray) {
                        McuLogic.mcuCommunicator?.sendCommand(cmdType, data, false)
                    })
                } else {
                    McuLogic.mcuCommunicator!!.startBeat()
                    brightnessObserver.startObservingBrightness()
                }

                parseMcuEvent.idleEvent.armBackTapper()
            }
        }
    }

    private val onMcuEventAction = McuCommunicator.McuAction { cmdType, data ->
        Thread {
            val event = parseMcuEvent.getMcuEvent(cmdType, data)
            eventAction.processAction(cmdType, data, event, config)

            mcuEventListener?.update(event, cmdType, data)
        }.start()
    }

    fun startMcuReader() {
        eventAction = EventAction(context)
        parseMcuEvent.carDataEvent = CarDataEvent
        parseMcuEvent.benzDataEvent = BenzDataEvent

        parseMcuEvent.screenSwitchEvent = if (PowerManagerApp.getSettingsInt("CarDisplay") == 0) {
            ScreenSwitchEventNoOEMScreen
        } else {
            ScreenSwitchEvent
        }
        AdbServiceConnection.startKsw(context)
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
        parseMcuEvent.idleEvent.clearBackTapper()
        McuLogic.stopAutoVolume()
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
        mcuEventListener = listener
    }

    fun unregisterMcuEventListener() {
        mcuEventListener = null
    }
}