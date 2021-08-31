package com.snaggly.ksw_toolkit.core.config

import com.google.gson.Gson
import com.snaggly.ksw_toolkit.core.config.beans.*
import java.io.*

class ConfigManager private constructor() {
    private var configFile: File? = null
    var systemOptions = SystemOptions.initSystemTweaks()
    var eventManagers = EventManager.initStandardButtons()
    var json : String? = null
    private val gson = Gson()

    fun saveConfig() {
        json = gson.toJson(ConfigData(systemOptions, eventManagers))

        val fileWriter = FileWriter(configFile!!)
        fileWriter.write(json)
        fileWriter.close()
    }

    fun readConfig() {
        val fileReader = FileReader(configFile!!)
        json = fileReader.readText()
        fileReader.close()

        val configData = gson.fromJson(json, ConfigData::class.java)
        configData.systemOptions.startAtBoot?.let { systemOptions.startAtBoot = it }
        configData.systemOptions.hijackCS?.let { systemOptions.hijackCS = it }
        configData.systemOptions.soundRestorer?.let { systemOptions.soundRestorer = it }
        configData.systemOptions.autoTheme?.let { systemOptions.autoTheme = it }
        configData.systemOptions.autoVolume?.let { systemOptions.autoVolume = it }
        configData.systemOptions.maxVolume?.let { systemOptions.maxVolume = it }
        configData.systemOptions.logMcuEvent?.let { systemOptions.logMcuEvent = it }
        configData.systemOptions.interceptMcuCommand?.let { systemOptions.interceptMcuCommand = it }
        configData.systemOptions.extraMediaButtonHandle?.let { systemOptions.extraMediaButtonHandle = it }
        configData.systemOptions.nightBrightness?.let { systemOptions.nightBrightness = it }
        configData.systemOptions.nightBrightnessLevel?.let { systemOptions.nightBrightnessLevel = it }
        configData.systemOptions.mcuPath?.let { systemOptions.mcuPath = it }

        for (type in configData.eventManagers) {
            configData.eventManagers[type.key]?.eventMode!!.let { eventManagers[type.key]?.eventMode = it }
            configData.eventManagers[type.key]?.mcuCommandMode!!.let { eventManagers[type.key]?.mcuCommandMode = it }
            configData.eventManagers[type.key]?.appName!!.let { eventManagers[type.key]?.appName = it }
            configData.eventManagers[type.key]?.keyCode!!.let { eventManagers[type.key]?.keyCode = it }
        }
    }

    companion object {
        private const val fileName = "ksw-tk_config.json"
        private val config = ConfigManager()

        fun getConfig(filePath: String) : ConfigManager{
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