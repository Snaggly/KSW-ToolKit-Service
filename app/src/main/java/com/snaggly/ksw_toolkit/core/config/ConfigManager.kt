package com.snaggly.ksw_toolkit.core.config

import com.snaggly.ksw_toolkit.core.config.beans.*
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes
import com.snaggly.ksw_toolkit.util.enums.EventMode
import java.io.*

class ConfigManager private constructor() : IConfigBean {
    private var configFile: File? = null
    lateinit var systemTweaks: SystemTweaks private set
    val eventManagers = HashMap<EventManagerTypes, EventManager>()

    fun initBeans() {
        systemTweaks = SystemTweaks.initSystemTweaks(this)
        for (type in EventManagerTypes.values()) {
            eventManagers[type] = EventManager.initEventManager(this)
        }
    }

    override fun saveConfig() {
        val dataOutputStream = DataOutputStream(configFile!!.outputStream())

        dataOutputStream.writeBoolean(systemTweaks.startAtBoot.data)
        dataOutputStream.writeBoolean(systemTweaks.kswService.data)
        dataOutputStream.writeBoolean(systemTweaks.carDataLogging.data)
        dataOutputStream.writeBoolean(systemTweaks.autoVolume.data)
        dataOutputStream.writeBoolean(systemTweaks.maxVolume.data)
        dataOutputStream.writeBoolean(systemTweaks.hideTopBar.data)
        dataOutputStream.writeBoolean(systemTweaks.shrinkTopBar.data)
        dataOutputStream.writeInt(systemTweaks.dpi.data)

        for ((key, eventManager) in eventManagers) {
            dataOutputStream.writeUTF(key.name)
            dataOutputStream.writeInt(eventManager.eventMode.value)
            dataOutputStream.writeInt(eventManager.keyCode.data)
            dataOutputStream.writeUTF(eventManager.appName.data)
            dataOutputStream.writeInt(eventManager.mcuCommandMode.data)
        }

        dataOutputStream.close()
    }

    override fun readConfig() {
        val dataInputStream = DataInputStream(configFile!!.inputStream())

        systemTweaks = SystemTweaks(
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                dataInputStream.readInt(),
                this
        )

        while (dataInputStream.available() > 0) {
            eventManagers[EventManagerTypes.findByName(dataInputStream.readUTF())!!] = EventManager(
                    EventMode.findByValue(dataInputStream.readInt())!!,
                    dataInputStream.readInt(),
                    dataInputStream.readUTF(),
                    dataInputStream.readInt(),
                    this)
        }
    }

    companion object {
        private const val fileName = "ksw-tk_config.dat"
        private const val header = "SNAGGLY_KSW-T"
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
            return config
        }

        @Throws(UnsupportedEncodingException::class)
        fun importConfig(applicationFilePath: String, toImportFilePath: String) : ConfigManager {
            val inputFile = DataInputStream(File(toImportFilePath).inputStream())
            if (inputFile.readUTF() != header)
                throw UnsupportedEncodingException("Header mismatch!")
            inputFile.copyTo(File("$applicationFilePath/$fileName").outputStream())
            config.configFile = null
            getConfig(applicationFilePath)
            return config
        }

        fun exportConfig(filePath: String) {
            val outputFile = DataOutputStream(File(filePath).outputStream())
            outputFile.writeUTF(header)
            config.configFile!!.inputStream().copyTo(outputFile)
            outputFile.close()
        }
    }
}