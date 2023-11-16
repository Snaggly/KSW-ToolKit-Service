package com.snaggly.ksw_toolkit.core.config

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.snaggly.ksw_toolkit.core.config.beans.AdvancedBrightness
import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.core.config.beans.SystemOptions
import com.snaggly.ksw_toolkit.core.service.CoreService
import com.snaggly.ksw_toolkit.util.list.eventtype.EventMode
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ConfigManager private constructor() {
    private var configFile: File? = null
    var systemOptions = SystemOptions.initSystemTweaks()
    var eventManagers = EventManager.initialButtons()
    var advancedBrightness = AdvancedBrightness.initDefault()
    val json: String?
        get() {
            return gson.toJson(ConfigData(systemOptions, eventManagers, advancedBrightness))
        }
    private val gson = Gson()

    fun saveConfig() {
        val fileWriter = FileWriter(configFile!!)
        fileWriter.write(json)
        fileWriter.close()
    }


    fun readConfig(context: Context) {
        try {
            val fileReader = FileReader(configFile!!)
            readConfig(fileReader.readText())
            fileReader.close()
        } catch (error: Exception) {
            CoreService.showAlertMessage(
                context,
                "The installed config file encountered an error! Dismiss to reset.\n${error.message}"
            )
            saveConfig()
        }
    }

    fun readConfig(json: String) {
        val root = JsonParser.parseString(json).asJsonObject

        //Parse SystemOptions
        root.get("systemOptions")?.takeIf { it.isJsonObject }?.asJsonObject?.let { sysOpt ->
            sysOpt.get("startAtBoot")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.startAtBoot = it.asBoolean }
            sysOpt.get("hijackCS")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.hijackCS = it.asBoolean }
            sysOpt.get("soundRestorer")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.soundRestorer = it.asBoolean }
            sysOpt.get("autoTheme")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.autoTheme = it.asBoolean }
            sysOpt.get("autoVolume")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.autoVolume = it.asBoolean }
            sysOpt.get("retainVolume")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.retainVolume = it.asBoolean }
            sysOpt.get("logMcuEvent")?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.logMcuEvent = it.asBoolean }
            sysOpt.get("interceptMcuCommand")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.interceptMcuCommand = it.asBoolean }
            sysOpt.get("extraMediaButtonHandle")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.extraMediaButtonHandle = it.asBoolean }
            sysOpt.get("nightBrightness")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.nightBrightness = it.asBoolean }
            sysOpt.get("nightBrightnessLevel")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }
                ?.let { systemOptions.nightBrightnessLevel = it.asInt }
            sysOpt.get("mcuPath")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
                ?.let { systemOptions.mcuPath = it.asString }
            sysOpt.get("tabletMode")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.tabletMode = it.asBoolean }
            sysOpt.get("hideStartMessage")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.hideStartMessage = it.asBoolean }
            sysOpt.get("decoupleNAVBtn")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { systemOptions.decoupleNAVBtn = it.asBoolean }
        }

        //Parse EventManagers
        root.get("eventManagers")?.takeIf { it.isJsonObject }?.asJsonObject?.let { evtMgrs ->
            for (eventManager in eventManagers) {
                evtMgrs.get(eventManager.key.name)?.takeIf { it.isJsonObject }?.let { evtMgr ->
                    evtMgr.asJsonObject.get("eventMode")
                        ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
                        ?.let { eventManager.value.eventMode = EventMode.valueOf(it.asString) }
                    evtMgr.asJsonObject.get("mcuCommandMode")
                        ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }
                        ?.let { eventManager.value.mcuCommandMode = it.asInt }
                    evtMgr.asJsonObject.get("appName")
                        ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
                        ?.let { eventManager.value.appName = it.asString }
                    evtMgr.asJsonObject.get("keyCode")
                        ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }
                        ?.let { eventManager.value.keyCode = it.asInt }
                    evtMgr.asJsonObject.get("taskerTaskName")
                        ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
                        ?.let { eventManager.value.taskerTaskName = it.asString }
                }
            }
        }

        //Parse AdvancesBrightness
        root.get("advancedBrightness")?.takeIf { it.isJsonObject }?.asJsonObject?.let { advBrg ->
            advBrg.get("isTimeBasedEnabled")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { advancedBrightness.isTimeBasedEnabled = it.asBoolean }
            advBrg.get("isUSBBasedEnabled")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { advancedBrightness.isUSBBasedEnabled = it.asBoolean }
            advBrg.get("sunriseAt")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
                ?.let { advancedBrightness.sunriseAt = it.asString }
            advBrg.get("sunsetAt")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
                ?.let { advancedBrightness.sunsetAt = it.asString }
            advBrg.get("autoTimes")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isBoolean }
                ?.let { advancedBrightness.autoTimes = it.asBoolean }
            advBrg.get("daylightBrightness")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }
                ?.let { advancedBrightness.daylightBrightness = it.asInt }
            advBrg.get("daylightHLBrightness")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }
                ?.let { advancedBrightness.daylightHLBrightness = it.asInt }
            advBrg.get("nightBrightnessLevel")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }
                ?.let { advancedBrightness.nightBrightnessLevel = it.asInt }
            advBrg.get("nightHLBrightnessLevel")
                ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isNumber }
                ?.let { advancedBrightness.nightHLBrightnessLevel = it.asInt }
        }
    }

    companion object {
        private const val fileName = "ksw-tk_config.json"
        private val config = ConfigManager()

        fun getConfig(context: Context): ConfigManager {
            val filePath = context.filesDir!!.absolutePath
            if (config.configFile != null) {
                return config
            }
            config.configFile = File("$filePath/$fileName")
            if (!config.configFile!!.isFile) {
                config.configFile!!.createNewFile()
                config.saveConfig()
            } else {
                config.readConfig(context)
            }
            return config
        }
    }
}