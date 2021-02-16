package com.snaggly.ksw_toolkit.core.service.mcu

import android.content.Context
import android.media.AudioManager
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbServiceConnection
import com.snaggly.ksw_toolkit.core.service.mcu.action.EventAction
import com.snaggly.ksw_toolkit.core.service.mcu.action.EventActionLogger
import com.snaggly.ksw_toolkit.core.service.mcu.parser.*
import com.snaggly.ksw_toolkit.core.service.sys_observers.BrightnessObserver
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.reader.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.reader.SerialReader

class McuReaderHandler(private val context: Context) {
    private val mcuEventListeners = ArrayList<McuEventObserver>()
    private val config = ConfigManager.getConfig(context.filesDir.absolutePath)
    private val brightnessObserver = BrightnessObserver(context)
    private val sendingInterceptor = McuSenderInterceptor()
    private lateinit var eventAction : EventAction
    private var parseMcuEvent = McuEvent(ScreenSwitchEvent, CarDataEvent, BenzDataEvent)
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

                if (config.systemTweaks.logMcuEvent.data)
                    eventAction = EventActionLogger(context)

                if (config.systemTweaks.carDataLogging.data) {
                    parseMcuEvent.carDataEvent = CarDataEventLogger
                    parseMcuEvent.benzDataEvent = BenzDataEvent
                }

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

                McuLogic.backTapper = BackTapper(context)
            }
        }
    }

    private val onMcuEventAction = McuCommunicator.McuAction { cmdType, data ->
        Thread {
            val event = parseMcuEvent.getMcuEvent(cmdType, data)
            eventAction.processAction(cmdType, data, event, config)

            for (mcuEventListener in mcuEventListeners)
                mcuEventListener.update(event, cmdType, data)
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