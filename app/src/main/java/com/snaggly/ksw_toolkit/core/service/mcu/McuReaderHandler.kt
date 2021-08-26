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
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.reader.SerialReader
import projekt.auto.mcu.ksw.serial.writer.SerialWriter

class McuReaderHandler(private val context: Context) {
    private var mcuEventListener : McuEventObserver? = null
    private val config = ConfigManager.getConfig(context.filesDir.absolutePath)
    private val brightnessObserver = BrightnessObserver(context)
    private val sendingInterceptor = McuSenderInterceptor(100)
    private lateinit var eventAction : EventAction
    private var parseMcuEvent = McuEvent(context)
    private var hasSerialInit = false

    init {
        when {
            Build.VERSION.RELEASE.contains("11") -> {
                McuLogic.mcuCommunicator = McuCommunicator(SerialWriter("/dev/ttyHS1"), LogcatReader())
            }
            Build.DISPLAY.contains("8937") -> {
                McuLogic.mcuCommunicator = McuCommunicator(SerialWriter("/dev/ttyHSL1"), LogcatReader())
            }
            else -> {
                McuLogic.mcuCommunicator = McuCommunicator.getInstance()
            }
        }
    }

    private val initialSerialStartAction = McuCommunicator.McuAction { cmdType, data ->
        if (!hasSerialInit) {
            //Only set up serial reader when in Sysmode 1
            if (data.isNotEmpty() && (cmdType == 0x1C && data[0] == 0x1.toByte())) {
                hasSerialInit = true
                McuLogic.mcuCommunicator!!.mcuReader.stopReading()
                AdbServiceConnection.stopKsw(context)

                //Initialize SerialReader
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

                //Check if Service should handle extra Media Buttons
                if (PowerManagerApp.getSettingsInt("CarDisplay") == 0) {
                    parseMcuEvent.screenSwitchEvent = ScreenSwitchEventNoOEMScreen
                } else {
                    val dataBytes : ByteArray
                    if (config.systemTweaks.extraMediaButtonHandle.data) {
                        parseMcuEvent.screenSwitchEvent = ScreenSwitchMediaHack
                        dataBytes = byteArrayOf(0x0e, 0x00)
                    } else {
                        parseMcuEvent.screenSwitchEvent = ScreenSwitchEvent
                        dataBytes = byteArrayOf(0x0e, 0x01)
                    }
                    McuLogic.mcuCommunicator?.sendCommand(0x70, dataBytes, false)
                }

                //Is AutoTheme on? This service will be able to toggle global Android Dark/Light Theme
                if (config.systemTweaks.autoTheme.data) {
                    parseMcuEvent.carDataEvent.lightEvent = LightEventSwitch.apply {
                        uiModeManager = context.getSystemService(UiModeManager::class.java)
                    }
                } else {
                    parseMcuEvent.carDataEvent.lightEvent = LightEvent
                }

                //Is NightBrightness on? This once Headlights turn on, the screen will dim to a given level.
                parseMcuEvent.carDataEvent.lightEvent.hasNightBrightness = config.systemTweaks.nightBrightness.data
                if (config.systemTweaks.nightBrightness.data) {
                    McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_On)
                    parseMcuEvent.carDataEvent.lightEvent.nightBrightnessLevel = config.systemTweaks.nightBrightnessLevel.data
                } else {
                    if (PowerManagerApp.getSettingsInt("Backlight_auto_set") == 0) {
                        McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_Off)
                    }
                }

                //Is McuLogging on? Useful for Tasker to get Mcu Data from Logcat. Replicates CenterService procedure.
                if (config.systemTweaks.logMcuEvent.data)
                    eventAction = EventActionLogger(context)

                McuLogic.mcuCommunicator!!.mcuReader.startReading(onMcuEventAction)

                //Should this service intercept what CenterService tries to send to Mcu? Replicated core CenterService commands.
                if (config.systemTweaks.interceptMcuCommand.data) {
                    sendingInterceptor.startReading(fun(cmdType: Int, data: ByteArray) {
                        McuLogic.mcuCommunicator?.sendCommand(cmdType, data, false)
                    })
                } else {
                    McuLogic.mcuCommunicator!!.startBeat()
                    brightnessObserver.startObservingBrightness()
                }

                parseMcuEvent.idleEvent.armBackTapper()

                //Get current CarData
                McuLogic.mcuCommunicator!!.sendCommand(104, byteArrayOf(5, 0), false)
            }
        }
    }

    private val onMcuEventAction = McuCommunicator.McuAction { cmdType, data ->
        Thread {
            if (cmdType != 0 || data.isNotEmpty()) {
                val event = parseMcuEvent.getMcuEvent(cmdType, data)
                eventAction.processAction(cmdType, data, event, config)

                mcuEventListener?.update(event, cmdType, data)
            }
        }.start()
    }

    fun startMcuReader() {
        eventAction = EventAction(context)

        //Start CenterService-McuService. If CenterService has to be hijacked, I need to wait until it's ready.
        AdbServiceConnection.startKsw(context)
        McuLogic.mcuCommunicator!!.mcuReader?.stopReading()
        McuLogic.mcuCommunicator!!.mcuReader = LogcatReader()
        if (config.systemTweaks.kswService.data) {
            McuLogic.mcuCommunicator!!.mcuReader.startReading(onMcuEventAction)
        } else {
            McuLogic.mcuCommunicator!!.mcuReader.startReading(initialSerialStartAction)
        }

        //Is AutoVolume on? Start on it on its separate thread. This only works when CarData get parsed!
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
        if (PowerManagerApp.getSettingsInt("Backlight_auto_set") == 0) {
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_Off)
        }
        if (config.systemTweaks.extraMediaButtonHandle.data && PowerManagerApp.getSettingsInt("CarDisplay") == 0) {
            val dataBytes = byteArrayOf(0x0e, 0x01)
            McuLogic.mcuCommunicator?.sendCommand(0x70, dataBytes, false)
        }
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