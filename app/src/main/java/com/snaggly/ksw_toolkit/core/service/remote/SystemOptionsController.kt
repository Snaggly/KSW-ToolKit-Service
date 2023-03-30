package com.snaggly.ksw_toolkit.core.service.remote

import com.snaggly.ksw_toolkit.ISystemOptionsControl
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.McuReaderHandler
import com.snaggly.ksw_toolkit.util.adb.DensityUtil

class SystemOptionsController(private val coreReaderHandler: McuReaderHandler) : ISystemOptionsControl.Stub() {
    private val configManager = coreReaderHandler.config

    override fun getNightBrightnessLevel(): Int {
        return configManager.systemOptions.nightBrightnessLevel!!
    }

    override fun setNightBrightnessLevel(value: Int) {
        configManager.systemOptions.nightBrightnessLevel = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getTabletMode(): Boolean {
        return configManager.systemOptions.tabletMode!!
    }

    override fun setTabletMode(value: Boolean) {
        configManager.systemOptions.tabletMode = value
        configManager.saveConfig()
        if (value)
            DensityUtil.turnOnTabletMode(coreReaderHandler.context)
        else
            DensityUtil.turnOffTabletMode(coreReaderHandler.context)
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
        coreReaderHandler.restartReader()
    }

    override fun getSoundRestorer(): Boolean {
        return configManager.systemOptions.soundRestorer!!
    }

    override fun setSoundRestorer(value: Boolean) {
        configManager.systemOptions.soundRestorer = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getAutoTheme(): Boolean {
        return configManager.systemOptions.autoTheme!!
    }

    override fun setAutoTheme(value: Boolean) {
        configManager.systemOptions.autoTheme = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getAutoVolume(): Boolean {
        return configManager.systemOptions.autoVolume!!
    }

    override fun setAutoVolume(value: Boolean) {
        configManager.systemOptions.autoVolume = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getRetainVolume(): Boolean {
        return configManager.systemOptions.retainVolume!!
    }

    override fun setRetainVolume(value: Boolean) {
        configManager.systemOptions.retainVolume = value
        McuLogic.retainVolumes = value
        configManager.saveConfig()
    }

    override fun getLogMcuEvent(): Boolean {
        return configManager.systemOptions.logMcuEvent!!
    }

    override fun setLogMcuEvent(value: Boolean) {
        configManager.systemOptions.logMcuEvent = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getInterceptMcuCommand(): Boolean {
        return configManager.systemOptions.interceptMcuCommand!!
    }

    override fun setInterceptMcuCommand(value: Boolean) {
        configManager.systemOptions.interceptMcuCommand = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getExtraMediaButtonHandle(): Boolean {
        return configManager.systemOptions.extraMediaButtonHandle!!
    }

    override fun setExtraMediaButtonHandle(value: Boolean) {
        configManager.systemOptions.extraMediaButtonHandle = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getNightBrightness(): Boolean {
        return configManager.systemOptions.nightBrightness!!
    }

    override fun setNightBrightness(value: Boolean) {
        configManager.systemOptions.nightBrightness = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getMcuPath(): String {
        return configManager.systemOptions.mcuPath!!
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
}