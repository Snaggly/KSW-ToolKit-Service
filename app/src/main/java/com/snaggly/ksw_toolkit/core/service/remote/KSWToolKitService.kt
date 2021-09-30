package com.snaggly.ksw_toolkit.core.service.remote

import android.app.Service
import android.content.Context
import android.content.Intent
import com.snaggly.ksw_toolkit.IKSWToolKitService
import com.snaggly.ksw_toolkit.IMcuListener
import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.core.service.CoreService
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.McuReaderHandler
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode

class KSWToolKitService(private val serviceContext: Context, private val coreReaderHandler: McuReaderHandler) : IKSWToolKitService.Stub() {

    private val configManager = coreReaderHandler.config

    override fun sendMcuCommand(cmdType: Int, data: ByteArray?): Boolean {
        if (!authenticate())
            return false

        return if (McuLogic.mcuCommunicator == null)
            false
        else {
            McuLogic.mcuCommunicator!!.sendCommand(cmdType, data, false)
            true
        }
    }

    override fun changeBtnConfig(btnType: Int, cmdType: Int, cmdValue: String?): Boolean {
        if (!authenticate())
            return false

        val eventConfig = configManager.eventManagers[EventManagerTypes.values()[btnType]]
        if (eventConfig == null)
            return false
        else {
            eventConfig.eventMode = EventMode.values()[cmdType]
            when (eventConfig.eventMode) {
                EventMode.NoAssignment -> {
                    eventConfig.appName = ""
                    eventConfig.keyCode = -1
                    eventConfig.mcuCommandMode = -1
                }
                EventMode.KeyEvent -> {
                    try {
                        val cmdValueInt = cmdValue!!.toInt()
                        eventConfig.appName = ""
                        eventConfig.keyCode = cmdValueInt
                        eventConfig.mcuCommandMode = -1
                    } catch (e: Exception) {
                        eventConfig.eventMode = EventMode.NoAssignment
                        return false
                    }
                }
                EventMode.StartApp -> {
                    try {
                        eventConfig.appName = cmdValue!!
                        eventConfig.keyCode = -1
                        eventConfig.mcuCommandMode = -1
                    } catch (e: Exception) {
                        eventConfig.eventMode = EventMode.NoAssignment
                        return false
                    }
                }
                EventMode.McuCommand -> {
                    try {
                        val cmdValueInt = cmdValue!!.toInt()
                        eventConfig.appName = ""
                        eventConfig.keyCode = -1
                        eventConfig.mcuCommandMode = cmdValueInt
                    } catch (e: Exception) {
                        eventConfig.eventMode = EventMode.NoAssignment
                        return false
                    }
                }
            }
        }
        configManager.saveConfig()
        return true
    }

    override fun setDefaultBtnLayout() {
        configManager.eventManagers = EventManager.initStandardButtons()
        configManager.saveConfig()
    }

    override fun getConfig(): String? {
        return configManager.json
    }

    override fun setOptions(allSettings: BooleanArray?): Boolean {
        if (!authenticate())
            return false
        if (allSettings == null)
            return false
        if (allSettings.size < 10) {
            return false
        }

        configManager.systemOptions.startAtBoot = allSettings[0]
        configManager.systemOptions.hijackCS = allSettings[1]
        configManager.systemOptions.soundRestorer = allSettings[2]
        configManager.systemOptions.autoTheme = allSettings[3]
        configManager.systemOptions.autoVolume = allSettings[4]
        configManager.systemOptions.maxVolume = allSettings[5]
        configManager.systemOptions.logMcuEvent = allSettings[6]
        configManager.systemOptions.interceptMcuCommand = allSettings[7]
        configManager.systemOptions.extraMediaButtonHandle = allSettings[8]
        configManager.systemOptions.nightBrightness = allSettings[9]

        configManager.saveConfig()

        coreReaderHandler.restartReader()
        return true
    }

    override fun getSettingsTypes(): Array<String>? {
        return arrayOf(
            "Enabled",
            "HijackCenterService",
            "SoundRestorer",
            "AutoTheme",
            "AutoVolume",
            "MaxVolumeAtBoot",
            "LogMcuEvents",
            "InterceptMcuCommands",
            "EnableExtendedIDrive",
            "NightBrightness"
        )
    }

    override fun setMcuPath(path: String?) : Boolean {
        return if (path != null) {
            configManager.systemOptions.mcuPath = path
            coreReaderHandler.restartReader()
            true
        } else {
            false
        }
    }

    override fun registerMcuListener(listener: IMcuListener?): Boolean {
        if (!authenticate())
            return false
        return if (listener != null) {
            coreReaderHandler.registerMcuEventListener(listener)
            true
        } else false
    }

    override fun unregisterMcuListener(listener: IMcuListener?): Boolean {
        return if (listener != null) {
            coreReaderHandler.unregisterMcuEventListener(listener)
            true
        } else false
    }

    override fun getNightBrightnessSetting(): Int {
        return configManager.systemOptions.nightBrightnessLevel!!
    }

    override fun setNightBrightnessSetting(value: Int) {
        configManager.systemOptions.nightBrightnessLevel = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    private fun authenticate() : Boolean {
        if (!ServiceValidation.hasAuthenticated) {
            return if (ServiceValidation.validate(serviceContext)) {
                true
            } else {
                (serviceContext as Service).stopSelf()
                false
            }
        }
        return true
    }
}