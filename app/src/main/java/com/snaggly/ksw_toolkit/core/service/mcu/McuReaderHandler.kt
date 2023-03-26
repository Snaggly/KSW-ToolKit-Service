package com.snaggly.ksw_toolkit.core.service.mcu

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.widget.Toast
import com.snaggly.ksw_toolkit.IMcuListener
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbServiceConnection
import com.snaggly.ksw_toolkit.core.service.mcu.action.EventAction
import com.snaggly.ksw_toolkit.core.service.mcu.action.EventActionLogger
import com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch.*
import com.snaggly.ksw_toolkit.core.service.mcu.parser.*
import com.snaggly.ksw_toolkit.core.service.sys_observers.BrightnessObserver
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.brightnesstools.AdvancedBrightnessHandler
import com.snaggly.ksw_toolkit.util.brightnesstools.AutoThemeManager
import com.snaggly.ksw_toolkit.util.brightnesstools.DaytimeObserver
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.reader.LogcatReader
import projekt.auto.mcu.ksw.serial.reader.SerialReader
import projekt.auto.mcu.ksw.serial.writer.SerialWriter
import kotlin.collections.ArrayList

class McuReaderHandler(val context: Context) {
    val config = ConfigManager.getConfig(context)

    private val backTapper = BackTapper(context)
    private var mcuEventListeners = ArrayList<IMcuListener>()
    private val brightnessObserver = BrightnessObserver(context)
    private val sendingInterceptor = McuSenderInterceptor(100)
    private var eventAction : EventAction? = null
    private var parseMcuEvent = McuEvent(context, backTapper)
    private val daytimeObserver = DaytimeObserver(context)
    private val autoThemeManager = AutoThemeManager(context)
    private var hasSerialInit = false @Synchronized get @Synchronized set

    init {
        when {
            config.systemOptions.mcuPath != "" -> {
                McuLogic.mcuCommunicator = CustomMcuCommunicator(backTapper,
                    SerialWriter(config.systemOptions.mcuPath),
                    LogcatReader())
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                McuLogic.mcuCommunicator =
                    CustomMcuCommunicator(backTapper, SerialWriter("/dev/ttyHS1"), LogcatReader())
            }
            Build.DISPLAY.contains("8937") -> {
                McuLogic.mcuCommunicator =
                    CustomMcuCommunicator(backTapper, SerialWriter("/dev/ttyHSL1"), LogcatReader())
            }
            else -> {
                McuLogic.mcuCommunicator = CustomMcuCommunicator(backTapper, SerialWriter(), LogcatReader())
            }
        }
    }

    private val initialSerialStartAction = McuCommunicator.McuAction { cmdType, data ->
        if (!hasSerialInit) {
            //Only set up serial reader when in Sysmode 1
            if (data.isNotEmpty() && (cmdType == 0x1C && data[0] == 0x1.toByte())) {
                hasSerialInit = true
                McuLogic.mcuCommunicator?.mcuReader?.stopReading()
                AdbServiceConnection.stopKsw(context)

                McuLogic.hasNoOEMScreen = PowerManagerApp.getSettingsInt("CarDisplay") == 0 && PowerManagerApp.getSettingsInt("OEM_FM") == 0
                McuLogic.hasBacklightAuto =  PowerManagerApp.getSettingsInt("Backlight_auto_set") == 0

                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SYS_SCREEN_ON) //For debugging :c

                //Create new ScreenSwitchEvent object to make sure old one dies.
                parseMcuEvent.screenSwitchEvent = ScreenSwitchEvent(backTapper)

                //Check if Service should handle extra Media Buttons - If no OEM screen this already is active
                if (!McuLogic.hasNoOEMScreen) {
                    if (config.systemOptions.extraMediaButtonHandle == true) {
                        parseMcuEvent.screenSwitchEvent.addAction(MediaBtnHack())
                    }
                }
                else {
                    parseMcuEvent.screenSwitchEvent.addAction(NoOEMScreen(context))
                }

                //Is SoundRestorer active?
                if (config.systemOptions.soundRestorer == true)
                    parseMcuEvent.screenSwitchEvent.addAction(SoundRestorer())

                //Is NavBtnDecoupler on?
                if (config.systemOptions.decoupleNAVBtn == true)
                    parseMcuEvent.screenSwitchEvent.addAction(NAVBtnDecoupler())

                //Perform screen switch commands.
                parseMcuEvent.screenSwitchEvent.getScreenSwitch(byteArrayOf(0,1))

                //Is McuLogging on? Useful for Tasker to get Mcu Data from Logcat. Replicates CenterService procedure.
                eventAction = if (config.systemOptions.logMcuEvent == true)
                    EventActionLogger(context)
                else
                    EventAction(context)

                //Initialize SerialReader
                when {
                    config.systemOptions.mcuPath != "" -> {
                        McuLogic.mcuCommunicator?.mcuReader =
                            SerialReader(config.systemOptions.mcuPath)
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        McuLogic.mcuCommunicator?.mcuReader = SerialReader("/dev/ttyHS1")
                    }
                    Build.DISPLAY.contains("8937") -> {
                        McuLogic.mcuCommunicator?.mcuReader = SerialReader("/dev/ttyHSL1")
                    }
                    else -> {
                        McuLogic.mcuCommunicator?.mcuReader = SerialReader()
                    }
                }
                McuLogic.mcuCommunicator?.mcuReader?.startReading(onMcuEventAction)

                //Get current CarData
                Thread{
                    while (!McuLogic.hasInterceptedCarData) {
                        McuLogic.mcuCommunicator?.sendCommand(104, byteArrayOf(5, 0), false)
                        Thread.sleep(500)
                    }
                }.start()

                //Should this service intercept what CenterService tries to send to Mcu? Replicated core CenterService commands.
                if (config.systemOptions.interceptMcuCommand == true) {
                    sendingInterceptor.startReading(fun(cmdType: Int, data: ByteArray) {
                        McuLogic.mcuCommunicator?.sendCommand(cmdType, data, false)
                    })
                } else {
                    McuLogic.mcuCommunicator?.startBeat()
                    //Commented out because even if the light is on, it is only momentarily turned to normal brightness
                    // when KSW-ToolKit is started
                    //No sign of being called even when the service is running
                    //brightnessObserver.startObservingBrightness()
                }

                //Toggle AdvancedBrightness
                parseMcuEvent.carDataEvent.lightEvent.advancedBrightnessHandler = AdvancedBrightnessHandler.getHandler(context, daytimeObserver)

                //Is AutoTheme on? This service will be able to toggle global Android Dark/Light Theme
                if (config.systemOptions.autoTheme == true) {
                    parseMcuEvent.carDataEvent.lightEvent.autoThemeManager = { autoThemeManager.handleThemeChangeByLightEvent(it) }
                    daytimeObserver.registerDaytimeListener {
                        autoThemeManager.handleThemeChangeByTime(it != DaytimeObserver.Daytime.Day)
                    }
                } else {
                    parseMcuEvent.carDataEvent.lightEvent.autoThemeManager = null
                }
            }
        }
    }

    val onMcuEventAction = McuCommunicator.McuAction { cmdType, data ->
        Thread {
            if (cmdType != 0 || data.isNotEmpty()) {
                val event = parseMcuEvent.getMcuEvent(cmdType, data)
                eventAction?.processAction(cmdType, data, event, config)

                mcuEventListeners.forEach {
                    try {
                        it.updateMcu(event.toString(), cmdType, data)
                    } catch (e: Exception) {
                        unregisterMcuEventListener(it)
                    }
                }
            }
        }.start()
    }

    fun startMcuReader() {
        eventAction = EventAction(context)

        //Start CenterService-McuService. If CenterService has to be hijacked, I need to wait until it's ready.
        AdbServiceConnection.startKsw(context)
        McuLogic.mcuCommunicator?.mcuReader?.stopReading()
        McuLogic.mcuCommunicator?.mcuReader = LogcatReader()
        if (config.systemOptions.hijackCS == true) {
            McuLogic.mcuCommunicator?.mcuReader?.startReading(initialSerialStartAction)
        } else {
            McuLogic.mcuCommunicator?.mcuReader?.startReading(onMcuEventAction)
        }

        //Is AutoVolume on? Start on it on its separate thread. This only works when CarData get parsed!
        if (config.systemOptions.autoVolume == true) {
            McuLogic.startAutoVolume(context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        }
    }

    fun stopReader() {
        daytimeObserver.clearAllListeners()
        parseMcuEvent.screenSwitchEvent.clearActions()
        //brightnessObserver.stopObservingBrightness()
        parseMcuEvent.carDataEvent.lightEvent.advancedBrightnessHandler?.destroy()
        backTapper.removeBackWindow()
        McuLogic.stopAutoVolume()
        McuLogic.mcuCommunicator?.stopBeat()
        sendingInterceptor.stopReading()
        McuLogic.mcuCommunicator?.mcuReader?.stopReading()
        if (PowerManagerApp.getSettingsInt("Backlight_auto_set") == 0) {
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_Off)
        }
        hasSerialInit = false
    }

    fun restartReader() {
        stopReader()
        startMcuReader()
    }

    fun registerMcuEventListener(listener: IMcuListener) {
        mcuEventListeners.forEach {
            if (listener.asBinder() == it.asBinder())
                return
        }
        mcuEventListeners.add(listener)
    }

    fun unregisterMcuEventListener(listener: IMcuListener?) {
        mcuEventListeners.removeAll{ it.asBinder() == listener?.asBinder() }
    }

    fun unregisterAllMcuEventListeners() {
        mcuEventListeners.clear()
    }

    fun showStartMessage() {
        if (config.systemOptions.hideStartMessage == false) {
            "KSW-ToolKit-Service started".showMessage()
        }
    }

    fun String.showMessage() {
        Toast.makeText(context, this, Toast.LENGTH_LONG).show()
    }

    fun reTriggerBrightness() {
        parseMcuEvent.carDataEvent.lightEvent.advancedBrightnessHandler?.trigger()
    }

    fun triggerTestHLOn() {
        onMcuEventAction.update(0xA1, byteArrayOf(0x10, 7, 1))
    }

    fun triggerTestHLOff() {
        onMcuEventAction.update(0xA1, byteArrayOf(0x10, 0, 1))
    }

    fun testBtnEvent(testCode: Byte) {
        onMcuEventAction.update(0xA1, byteArrayOf(0x17, testCode, 0x1))
    }
}