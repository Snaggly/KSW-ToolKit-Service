package com.snaggly.ksw_toolkit.core.config

import com.snaggly.ksw_toolkit.core.config.beans.*
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes
import com.snaggly.ksw_toolkit.util.enums.EventMode
import java.io.*

class ConfigManager private constructor() : IConfigBean {
    private lateinit var configFile: File
    lateinit var soundRestorer: SoundRestorer private set
    lateinit var systemTweaks: SystemTweaks private set
    lateinit var mcuListener: McuListener private set
    val eventManagers = HashMap<EventManagerTypes, EventManager>()

    fun initBeans() {
        soundRestorer = SoundRestorer.initSoundRestorer(this)
        systemTweaks = SystemTweaks.initSystemTweaks(this)
        mcuListener = McuListener.initMcuListener(this)
        for (type in EventManagerTypes.values()) {
            eventManagers[type] = EventManager.initEventManager(this)
        }
    }

    override fun saveConfig() {
        val dataOutputStream = DataOutputStream(configFile.outputStream())
        dataOutputStream.writeBoolean(soundRestorer.startOnBoot.data)
        dataOutputStream.writeBoolean(soundRestorer.initVolumesAtBoot.data)
        dataOutputStream.writeBoolean(soundRestorer.keepOEMRadio.data)
        dataOutputStream.writeBoolean(soundRestorer.forceSoundInOEM.data)

        dataOutputStream.writeBoolean(systemTweaks.hideTopBar.data)
        dataOutputStream.writeBoolean(systemTweaks.shrinkTopBar.data)

        dataOutputStream.writeBoolean(mcuListener.stopKswServiceOnBoot.data)
        dataOutputStream.writeInt(mcuListener.mcuSource.data)

        for ((key, eventManager) in eventManagers) {
            dataOutputStream.writeUTF(key.name)
            dataOutputStream.writeInt(eventManager.eventMode.value)
            dataOutputStream.writeInt(eventManager.keyCode.data)
            dataOutputStream.writeUTF(eventManager.appName.data)
        }

        dataOutputStream.close()
    }

    override fun readConfig() {
        val dataInputStream = DataInputStream(configFile.inputStream())
        soundRestorer = SoundRestorer(
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                this
        )

        systemTweaks = SystemTweaks(
                dataInputStream.readBoolean(),
                dataInputStream.readBoolean(),
                this
        )

        mcuListener = McuListener(
                dataInputStream.readBoolean(),
                dataInputStream.readInt(),
                this
        )

        while (dataInputStream.available() > 0) {
            eventManagers[EventManagerTypes.findByName(dataInputStream.readUTF())!!] = EventManager(
                    EventMode.findByValue(dataInputStream.readInt())!!,
                    dataInputStream.readInt(),
                    dataInputStream.readUTF(),
                    this)
        }
    }

    companion object {
        private const val fileName = "ksw-tk_config.dat"
        private const val header = "SNAGGLY_KSW-T"
        private val config = ConfigManager()

        fun getConfig(filePath: String) : ConfigManager{
            config.configFile = File("$filePath/$fileName")
            if (!config.configFile.isFile)
                config.configFile.createNewFile()
            try {
                config.readConfig()
            }
            catch (ioe: IOException) {
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
            return getConfig(applicationFilePath)
        }

        fun exportConfig(filePath: String, config: ConfigManager) {
            val outputFile = DataOutputStream(File(filePath).outputStream())
            outputFile.writeUTF(header)
            config.configFile.inputStream().copyTo(outputFile)
            outputFile.close()
        }
    }
}