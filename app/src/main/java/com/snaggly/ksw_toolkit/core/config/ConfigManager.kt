package com.snaggly.ksw_toolkit.core.config

import android.content.Context
import com.google.gson.Gson
import com.snaggly.ksw_toolkit.core.config.beans.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ConfigManager private constructor() {
    private var configFile: File? = null
    var systemOptions = SystemOptions.initSystemTweaks()
    var eventManagers = EventManager.initialButtons()
    var advancedBrightness = AdvancedBrightness.initDefault()
    var json : String? = null
    private val gson = Gson()

    fun saveConfig() {
        json = gson.toJson(ConfigData(systemOptions, eventManagers, advancedBrightness))

        val fileWriter = FileWriter(configFile!!)
        fileWriter.write(json)
        fileWriter.close()
    }


    fun readConfig() {
        val fileReader = FileReader(configFile!!)
        json = fileReader.readText()
        fileReader.close()

        readConfig(json!!)
    }

    fun readConfig(json: String) {
        val configData = gson.fromJson(json, ConfigData::class.java)
        configData.systemOptions?.let { sysOpt ->
            sysOpt.startAtBoot?.let { systemOptions.startAtBoot = it }
            sysOpt.hijackCS?.let { systemOptions.hijackCS = it }
            sysOpt.soundRestorer?.let { systemOptions.soundRestorer = it }
            sysOpt.autoTheme?.let { systemOptions.autoTheme = it }
            sysOpt.autoVolume?.let { systemOptions.autoVolume = it }
            sysOpt.maxVolume?.let { systemOptions.maxVolume = it }
            sysOpt.logMcuEvent?.let { systemOptions.logMcuEvent = it }
            sysOpt.interceptMcuCommand?.let { systemOptions.interceptMcuCommand = it }
            sysOpt.extraMediaButtonHandle?.let { systemOptions.extraMediaButtonHandle = it }
            sysOpt.nightBrightness?.let { systemOptions.nightBrightness = it }
            sysOpt.nightBrightnessLevel?.let { systemOptions.nightBrightnessLevel = it }
            sysOpt.mcuPath?.let { systemOptions.mcuPath = it }
            sysOpt.tabletMode?.let { systemOptions.tabletMode = it }
            sysOpt.hideStartMessage?.let { systemOptions.hideStartMessage = it }
            sysOpt.decoupleNAVBtn?.let { systemOptions.decoupleNAVBtn = it }
        }

        configData.eventManagers?.let { evtMgr ->
            for (type in eventManagers) {
                evtMgr[type.key]?.eventMode?.let { eventManagers[type.key]?.eventMode = it }
                evtMgr[type.key]?.mcuCommandMode?.let { eventManagers[type.key]?.mcuCommandMode = it }
                evtMgr[type.key]?.appName?.let { eventManagers[type.key]?.appName = it }
                evtMgr[type.key]?.keyCode?.let { eventManagers[type.key]?.keyCode = it }
                evtMgr[type.key]?.taskerTaskName?.let { eventManagers[type.key]?.taskerTaskName = it }
            }
        }

        configData.advancedBrightness?.let { confAdvBrightness ->
            confAdvBrightness.isTimeBasedEnabled?.let { advancedBrightness.isTimeBasedEnabled = it }
            confAdvBrightness.isUSBBasedEnabled?.let {  advancedBrightness.isUSBBasedEnabled = it }
            confAdvBrightness.sunriseAt?.let {  advancedBrightness.sunriseAt = it }
            confAdvBrightness.sunsetAt?.let {  advancedBrightness.sunsetAt = it }
            confAdvBrightness.autoTimes?.let {  advancedBrightness.autoTimes = it }
            confAdvBrightness.daylightBrightness?.let {  advancedBrightness.daylightBrightness = it }
            confAdvBrightness.daylightHLBrightness?.let {  advancedBrightness.daylightHLBrightness = it }
            confAdvBrightness.nightBrightnessLevel?.let {  advancedBrightness.nightBrightnessLevel = it }
            confAdvBrightness.nightHLBrightnessLevel?.let {  advancedBrightness.nightHLBrightnessLevel = it }
        }
    }

    companion object {
        private const val fileName = "ksw-tk_config.json"
        private val config = ConfigManager()

        fun getConfig(context: Context) : ConfigManager{
            val filePath = context.filesDir!!.absolutePath
            if (config.configFile != null) {
                return config
            }
            config.configFile = File("$filePath/$fileName")
            if (!config.configFile!!.isFile) {
                config.configFile!!.createNewFile()
                config.saveConfig()
            }

            config.readConfig()
            return config
        }
    }
}