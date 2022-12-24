package com.snaggly.ksw_toolkit.core.service.mcu

import android.app.UiModeManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.widget.Toast
import com.snaggly.ksw_toolkit.IMcuListener
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.service.adb.AdbServiceConnection
import com.snaggly.ksw_toolkit.core.service.mcu.action.EventAction
import com.snaggly.ksw_toolkit.core.service.mcu.action.EventActionLogger
import com.snaggly.ksw_toolkit.core.service.mcu.action.SoundRestorer
import com.snaggly.ksw_toolkit.core.service.mcu.parser.*
import com.snaggly.ksw_toolkit.core.service.sys_observers.BrightnessObserver
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.reader.LogcatReader
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.reader.SerialReader
import projekt.auto.mcu.ksw.serial.writer.SerialWriter

class McuReaderHandler(private val context: Context) {
    private val backTapper = BackTapper(context)
    private var mcuEventListeners = ArrayList<IMcuListener>()
    private val brightnessObserver = BrightnessObserver(context)
    private val sendingInterceptor = McuSenderInterceptor(100)
    private var eventAction : EventAction? = null
    private var parseMcuEvent = McuEvent(context, backTapper)
    private var hasSerialInit = false @Synchronized get @Synchronized set

    private val config = ConfigManager.getConfig(context.filesDir.absolutePath)

    init {
        when {
            config.systemOptions.mcuPath != "" -> {
                McuLogic.mcuCommunicator = CustomMcuCommunicator(backTapper, SerialWriter(config.systemOptions.mcuPath), LogcatReader())
            }
            Build.VERSION.SDK_INT >= 30 -> {
                McuLogic.mcuCommunicator = CustomMcuCommunicator(backTapper, SerialWriter("/dev/ttyHS1"), LogcatReader())
            }
            Build.DISPLAY.contains("8937") -> {
                McuLogic.mcuCommunicator = CustomMcuCommunicator(backTapper, SerialWriter("/dev/ttyHSL1"), LogcatReader())
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

                //Check if Service should handle extra Media Buttons - If no OEM screen this already is active
                val dataBytes : ByteArray
                if (!McuLogic.hasNoOEMScreen) {
                    if (config.systemOptions.extraMediaButtonHandle!!) {
                        parseMcuEvent.screenSwitchEvent = ScreenSwitchMediaHack(backTapper)
                        dataBytes = byteArrayOf(0x0e, 0x00)
                    }
                    else {
                        parseMcuEvent.screenSwitchEvent = ScreenSwitchEvent(backTapper)
                        dataBytes = byteArrayOf(0x0e, 0x01)
                    }
                }
                else {
                    parseMcuEvent.screenSwitchEvent = ScreenSwitchEventNoOEMScreen(context, backTapper)
                    dataBytes = byteArrayOf(0x0e, 0x00)
                }
                McuLogic.mcuCommunicator?.sendCommand(0x70, dataBytes, false)

                //Is SoundRestorer active?
                parseMcuEvent.screenSwitchEvent.soundRestorer = if (config.systemOptions.soundRestorer!!)
                    SoundRestorer.HasSoundRestorer
                else
                    SoundRestorer.NoSoundRestorer
                parseMcuEvent.screenSwitchEvent.soundRestorer.reEnableSound()

                //Is NightBrightness on? This once Headlights turn on, the screen will dim to a given level.
                if (config.systemOptions.nightBrightness!!) {
                    McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_On)
                    McuLogic.nightBrightness = config.systemOptions.nightBrightnessLevel!!
                } else {
                    if (McuLogic.hasBacklightAuto) {
                        McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_Off)
                    }
                    McuLogic.nightBrightness = -1
                }

                //Is AutoTheme on? This service will be able to toggle global Android Dark/Light Theme
                if (config.systemOptions.autoTheme!!) {
                    parseMcuEvent.carDataEvent.lightEvent = LightEventSwitch(context).apply {
                        uiModeManager = context.getSystemService(UiModeManager::class.java)
                    }
                } else {
                    parseMcuEvent.carDataEvent.lightEvent = LightEvent(context)
                }

                //Is McuLogging on? Useful for Tasker to get Mcu Data from Logcat. Replicates CenterService procedure.
                eventAction = if (config.systemOptions.logMcuEvent!!)
                    EventActionLogger(context)
                else
                    EventAction(context)

                //Initialize SerialReader
                when {
                    config.systemOptions.mcuPath != "" -> {
                        McuLogic.mcuCommunicator?.mcuReader = SerialReader(config.systemOptions.mcuPath)
                    }
                    Build.VERSION.SDK_INT >= 30 -> {
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
                if (config.systemOptions.interceptMcuCommand!!) {
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
        if (config.systemOptions.autoVolume!!) {
            McuLogic.startAutoVolume(context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        }
    }

    fun stopReader() {
        //brightnessObserver.stopObservingBrightness()
        backTapper.removeBackWindow()
        McuLogic.stopAutoVolume()
        McuLogic.mcuCommunicator?.stopBeat()
        sendingInterceptor.stopReading()
        McuLogic.mcuCommunicator?.mcuReader?.stopReading()
        if (PowerManagerApp.getSettingsInt("Backlight_auto_set") == 0) {
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_Off)
        }
        if (config.systemOptions.extraMediaButtonHandle!! && !McuLogic.hasNoOEMScreen) {
            val dataBytes = byteArrayOf(0x0e, 0x01)
            McuLogic.mcuCommunicator?.sendCommand(0x70, dataBytes, false)
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
        Toast.makeText(context ,this, Toast.LENGTH_LONG).show();
    }
}