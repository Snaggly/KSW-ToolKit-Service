package com.snaggly.ksw_toolkit.core.config

import com.google.gson.Gson
import com.snaggly.ksw_toolkit.core.config.beans.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ConfigManager private constructor() {
    private var configFile: File? = null
    var systemOptions = SystemOptions.initSystemTweaks()
    var eventManagers = EventManager.initialButtons()
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
        }

        configData.eventManagers?.let { eventManagers ->
            for (type in eventManagers) {
                eventManagers[type.key]?.eventMode!!.let { eventManagers[type.key]?.eventMode = it }
                eventManagers[type.key]?.mcuCommandMode!!.let { eventManagers[type.key]?.mcuCommandMode = it }
                eventManagers[type.key]?.appName!!.let { eventManagers[type.key]?.appName = it }
                eventManagers[type.key]?.keyCode!!.let { eventManagers[type.key]?.keyCode = it }
                eventManagers[type.key]?.taskerTaskName!!.let { eventManagers[type.key]?.taskerTaskName = it }
            }
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