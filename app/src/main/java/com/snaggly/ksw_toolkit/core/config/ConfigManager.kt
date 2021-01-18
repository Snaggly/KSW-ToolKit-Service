package com.snaggly.ksw_toolkit.core.config

import com.snaggly.ksw_toolkit.core.config.beans.*
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File

class ConfigManager private constructor() : IConfigBean {
    lateinit var configFile: File private set
    var soundRestorer: SoundRestorer private set
    var systemTweaks: SystemTweaks private set
    var mcuListener: McuListener private set
    val eventManagers = ArrayList<EventManager>()

    init {
        soundRestorer = SoundRestorer.initSoundRestorer(this)
        systemTweaks = SystemTweaks.initSystemTweaks(this)
        mcuListener = McuListener.initMcuListener(this)
        for (type in EventManagerTypes.values()) {
            eventManagers.add(EventManager.initEventManager(this))
        }
    }

    override fun saveConfig() {

    }

    companion object {
        private const val fileName = "ksw-tk_config.dat"
        private val config = ConfigManager()

        fun getConfig(filePath: String) : ConfigManager{
            val file = File("$filePath/$fileName")
            if (!file.isFile) {
                file.createNewFile()
                config.configFile = file
                config.saveConfig()
            } else {

            }

            return config
        }

        fun importConfig(filePath: String) : ConfigManager{

            return ConfigManager()
        }

        fun exportConfig(filePath: String, config: ConfigManager) {

        }
    }
}