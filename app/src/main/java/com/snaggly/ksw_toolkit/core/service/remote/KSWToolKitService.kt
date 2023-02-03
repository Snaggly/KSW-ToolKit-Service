package com.snaggly.ksw_toolkit.core.service.remote

import android.app.Service
import android.content.Context
import com.snaggly.ksw_toolkit.IKSWToolKitService
import com.snaggly.ksw_toolkit.IMcuListener
import com.snaggly.ksw_toolkit.core.config.ConfigManager
import com.snaggly.ksw_toolkit.core.config.beans.AdvancedBrightness
import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.McuReaderHandler
import com.snaggly.ksw_toolkit.util.adb.DensityUtil
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode

class KSWToolKitService(private val serviceContext: Context, private val coreReaderHandler: McuReaderHandler?) : IKSWToolKitService.Stub() {

    private val configManager = coreReaderHandler?.config ?: ConfigManager.getConfig(serviceContext)

    override fun sendMcuCommand(cmdType: Int, data: ByteArray?): Boolean {
        if (!authenticate() || data == null)
            return false

        return if (McuLogic.mcuCommunicator == null)
            false
        else {
            McuLogic.mcuCommunicator?.sendCommand(cmdType, data, false)
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
                    eventConfig.taskerTaskName = ""
                }
                EventMode.KeyEvent -> {
                    try {
                        val cmdValueInt = cmdValue!!.toInt()
                        eventConfig.appName = ""
                        eventConfig.keyCode = cmdValueInt
                        eventConfig.mcuCommandMode = -1
                        eventConfig.taskerTaskName = ""
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
                        eventConfig.taskerTaskName = ""
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
                        eventConfig.taskerTaskName = ""
                    } catch (e: Exception) {
                        eventConfig.eventMode = EventMode.NoAssignment
                        return false
                    }
                }
                EventMode.TaskerTask -> {
                    try {
                        eventConfig.appName = ""
                        eventConfig.keyCode = -1
                        eventConfig.mcuCommandMode = -1
                        eventConfig.taskerTaskName = cmdValue!!
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

    override fun setConfig(configJson: String?): Boolean {
        if (!authenticate())
            return false
        if (configJson == null)
            return false

        configManager.readConfig(configJson)
        configManager.saveConfig()

        coreReaderHandler?.restartReader()
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
            "NightBrightness",
            "HideStartMessage"
        )
    }

    override fun setMcuPath(path: String?) : Boolean {
        return if (path != null) {
            configManager.systemOptions.mcuPath = path
            coreReaderHandler?.restartReader()
            true
        } else {
            false
        }
    }

    override fun registerMcuListener(listener: IMcuListener?): Boolean {
        if (!authenticate())
            return false
        return if (listener != null) {
            coreReaderHandler?.registerMcuEventListener(listener)
            true
        } else false
    }

    override fun unregisterMcuListener(listener: IMcuListener?): Boolean {
        return if (listener != null) {
            coreReaderHandler?.unregisterMcuEventListener(listener)
            true
        } else false
    }

    override fun getNightBrightnessLevel(): Int {
        return configManager.systemOptions.nightBrightnessLevel!!
    }

    override fun setNightBrightnessLevel(value: Int) {
        configManager.systemOptions.nightBrightnessLevel = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getTabletMode(): Boolean {
        return configManager.systemOptions.tabletMode!!
    }

    override fun setTabletMode(value: Boolean) {
        configManager.systemOptions.tabletMode = value
        configManager.saveConfig()
        if (value)
            DensityUtil.turnOnTabletMode(serviceContext)
        else
            DensityUtil.turnOffTabletMode(serviceContext)
    }

    override fun getHideStartMessage(): Boolean {
        return configManager.systemOptions.hideStartMessage!!
    }

    override fun setHideStartMessage(value: Boolean) {
        configManager.systemOptions.hideStartMessage = value
        configManager.saveConfig()
    }

    override fun getDecoupleNAVBtn(): Boolean {
        return configManager.systemOptions.decoupleNAVBtn?:false
    }

    override fun setDecoupleNAVBtn(value: Boolean) {
        configManager.systemOptions.decoupleNAVBtn = value

    override fun getAdvBri_IsTimeBased(): Boolean {
        return configManager.advancedBrightness.isTimeBasedEnabled!!
    }

    override fun setAdvBri_IsTimeBased(value: Boolean) {
        configManager.advancedBrightness.isTimeBasedEnabled = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAdvBri_IsUSBBased(): Boolean {
        return configManager.advancedBrightness.isUSBBasedEnabled!!
    }

    override fun setAdvBri_IsUSBBased(value: Boolean) {
        configManager.advancedBrightness.isUSBBasedEnabled = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAdvBri_SunriseAt(): String {
        return configManager.advancedBrightness.sunriseAt!!
    }

    override fun setAdvBri_SunriseAt(value: String?) {
        configManager.advancedBrightness.sunriseAt = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAdvBri_SunsetAt(): String {
        return configManager.advancedBrightness.sunsetAt!!
    }

    override fun setAdvBri_SunsetAt(value: String?) {
        configManager.advancedBrightness.sunsetAt = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAdvBri_Autotimes(): Boolean {
        return configManager.advancedBrightness.autoTimes!!
    }

    override fun setAdvBri_Autotimes(value: Boolean) {
        configManager.advancedBrightness.autoTimes = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAdvBri_DaylightBri(): Int {
        return configManager.advancedBrightness.daylightBrightness!!
    }

    override fun setAdvBri_DaylightBri(value: Int) {
        configManager.advancedBrightness.daylightBrightness = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAdvBri_DaylightHLBri(): Int {
        return configManager.advancedBrightness.daylightHLBrightness!!
    }

    override fun setAdvBri_DaylightHLBri(value: Int) {
        configManager.advancedBrightness.daylightHLBrightness = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAdvBri_NightBri(): Int {
        return configManager.advancedBrightness.nightBrightnessLevel!!
    }

    override fun setAdvBri_NightBri(value: Int) {
        configManager.advancedBrightness.nightBrightnessLevel = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAdvBri_NightHLBri(): Int {
        return configManager.advancedBrightness.nightHLBrightnessLevel!!
    }

    override fun setAdvBri_NightHLBri(value: Int) {
        configManager.advancedBrightness.nightHLBrightnessLevel = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getStartAtBoot(): Boolean {
        return configManager.systemOptions.startAtBoot!!
    }

    override fun setStartAtBoot(value: Boolean) {
        configManager.systemOptions.startAtBoot = value
        configManager.saveConfig()
    }

    override fun getHijackCS(): Boolean {
        return configManager.systemOptions.hijackCS!!
    }

    override fun setHijackCS(value: Boolean) {
        configManager.systemOptions.hijackCS = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getSoundRestorer(): Boolean {
        return configManager.systemOptions.soundRestorer!!
    }

    override fun setSoundRestorer(value: Boolean) {
        configManager.systemOptions.soundRestorer = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAutoTheme(): Boolean {
        return configManager.systemOptions.autoTheme!!
    }

    override fun setAutoTheme(value: Boolean) {
        configManager.systemOptions.autoTheme = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getAutoVolume(): Boolean {
        return configManager.systemOptions.autoVolume!!
    }

    override fun setAutoVolume(value: Boolean) {
        configManager.systemOptions.autoVolume = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getMaxVolume(): Boolean {
        return configManager.systemOptions.maxVolume!!
    }

    override fun setMaxVolume(value: Boolean) {
        configManager.systemOptions.maxVolume = value
        configManager.saveConfig()
    }

    override fun getLogMcuEvent(): Boolean {
        return configManager.systemOptions.logMcuEvent!!
    }

    override fun setLogMcuEvent(value: Boolean) {
        configManager.systemOptions.logMcuEvent = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getInterceptMcuCommand(): Boolean {
        return configManager.systemOptions.interceptMcuCommand!!
    }

    override fun setInterceptMcuCommand(value: Boolean) {
        configManager.systemOptions.interceptMcuCommand = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getExtraMediaButtonHandle(): Boolean {
        return configManager.systemOptions.extraMediaButtonHandle!!
    }

    override fun setExtraMediaButtonHandle(value: Boolean) {
        configManager.systemOptions.extraMediaButtonHandle = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getNightBrightness(): Boolean {
        return configManager.systemOptions.nightBrightness!!
    }

    override fun setNightBrightness(value: Boolean) {
        configManager.systemOptions.nightBrightness = value
        configManager.saveConfig()
        coreReaderHandler?.restartReader()
    }

    override fun getMcuPath(): String {
        return configManager.systemOptions.mcuPath!!
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