package com.snaggly.ksw_toolkit.core.service.remote

import com.snaggly.ksw_toolkit.IAdvancedBrightnessControl
import com.snaggly.ksw_toolkit.IAutoTimeListener
import com.snaggly.ksw_toolkit.core.service.mcu.McuReaderHandler
import com.snaggly.ksw_toolkit.util.brightnesstools.TimeBasedBrightness

class AdvancedBrightnessController(private val coreReaderHandler: McuReaderHandler) : IAdvancedBrightnessControl.Stub() {
    private val configManager = coreReaderHandler.config

    override fun getAdvBri_IsTimeBased(): Boolean {
        return configManager.advancedBrightness.isTimeBasedEnabled!!
    }

    override fun setAdvBri_IsTimeBased(value: Boolean) {
        configManager.advancedBrightness.isTimeBasedEnabled = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getAdvBri_IsUSBBased(): Boolean {
        return configManager.advancedBrightness.isUSBBasedEnabled!!
    }

    override fun setAdvBri_IsUSBBased(value: Boolean) {
        configManager.advancedBrightness.isUSBBasedEnabled = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getAdvBri_SunriseAt(): String {
        return configManager.advancedBrightness.sunriseAt!!
    }

    override fun setAdvBri_SunriseAt(value: String?) {
        configManager.advancedBrightness.sunriseAt = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getAdvBri_SunsetAt(): String {
        return configManager.advancedBrightness.sunsetAt!!
    }

    override fun setAdvBri_SunsetAt(value: String?) {
        configManager.advancedBrightness.sunsetAt = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getAdvBri_Autotimes(): Boolean {
        return configManager.advancedBrightness.autoTimes!!
    }

    override fun setAdvBri_Autotimes(value: Boolean) {
        configManager.advancedBrightness.autoTimes = value
        configManager.saveConfig()
        coreReaderHandler.restartReader()
    }

    override fun getAdvBri_DaylightBri(): Int {
        return configManager.advancedBrightness.daylightBrightness!!
    }

    override fun setAdvBri_DaylightBri(value: Int) {
        configManager.advancedBrightness.daylightBrightness = value
        configManager.saveConfig()
        coreReaderHandler.reTriggerBrightness()
    }

    override fun getAdvBri_DaylightHLBri(): Int {
        return configManager.advancedBrightness.daylightHLBrightness!!
    }

    override fun setAdvBri_DaylightHLBri(value: Int) {
        configManager.advancedBrightness.daylightHLBrightness = value
        configManager.saveConfig()
        coreReaderHandler.reTriggerBrightness()
    }

    override fun getAdvBri_NightBri(): Int {
        return configManager.advancedBrightness.nightBrightnessLevel!!
    }

    override fun setAdvBri_NightBri(value: Int) {
        configManager.advancedBrightness.nightBrightnessLevel = value
        configManager.saveConfig()
        coreReaderHandler.reTriggerBrightness()
    }

    override fun getAdvBri_NightHLBri(): Int {
        return configManager.advancedBrightness.nightHLBrightnessLevel!!
    }

    override fun setAdvBri_NightHLBri(value: Int) {
        configManager.advancedBrightness.nightHLBrightnessLevel = value
        configManager.saveConfig()
        coreReaderHandler.reTriggerBrightness()
    }

    override fun registerAutoTimeListener(listener : IAutoTimeListener?) {
        listener?.let {
            TimeBasedBrightness.autoTimeListeners.forEach {
                if (listener.asBinder() == it.asBinder())
                    return
            }
            TimeBasedBrightness.autoTimeListeners.add(listener)
        }
    }

    override fun unregisterAutoTimeListener(listener : IAutoTimeListener?) {
        TimeBasedBrightness.autoTimeListeners.removeAll{ it.asBinder() == listener?.asBinder() }
    }
}