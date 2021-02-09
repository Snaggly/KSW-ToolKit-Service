package com.snaggly.ksw_toolkit.core.config

import com.google.gson.Gson
import com.snaggly.ksw_toolkit.core.config.beans.*
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes
import com.snaggly.ksw_toolkit.util.enums.EventMode
import java.io.*

class ConfigManager private constructor() : IConfigBean {
    private var configFile: File? = null
    lateinit var systemTweaks: SystemTweaks private set
    var eventManagers = HashMap<EventManagerTypes, EventManager>()
    val gson = Gson()

    fun initBeans() {
        systemTweaks = SystemTweaks.initSystemTweaks()
        for (type in EventManagerTypes.values()) {
            eventManagers[type] = EventManager.initEventManager()
        }
    }

    override fun saveConfig() {
        val json = gson.toJson(ConfigData(systemTweaks, eventManagers))

        val fileWriter = FileWriter(configFile!!)
        fileWriter.write(json)
        fileWriter.close()
    }

    override fun readConfig() {
        val fileReader = FileReader(configFile!!)
        val json = fileReader.readText()
        fileReader.close()

        val configData = gson.fromJson(json, ConfigData::class.java)
        systemTweaks = configData.systemTweaks
        eventManagers = configData.eventManagers
    }

    companion object {
        private const val fileName = "ksw-tk_config.json"
        private val config = ConfigManager()

        fun getConfig(filePath: String) : ConfigManager{
            if (config.configFile != null) {
                return config
            }
            config.configFile = File("$filePath/$fileName")
            if (!config.configFile!!.isFile)
                config.configFile!!.createNewFile()
            try {
                config.readConfig()
            }
            catch (ioe: Exception) {
                config.initBeans()
            }
            IConfigBean.configManager = config
            return config
        }

        @Throws(UnsupportedEncodingException::class)
        fun importConfig(applicationFilePath: String, toImportFilePath: String) : ConfigManager {
            val inputFile = File(toImportFilePath)
            inputFile.copyTo(File("$applicationFilePath/$fileName"))
            config.configFile = null
            getConfig(applicationFilePath)
            return config
        }

        fun exportConfig(filePath: String) {
            val outputFile = File(filePath)
            config.configFile!!.copyTo(outputFile)
        }
    }
}