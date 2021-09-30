package com.snaggly.ksw_toolkit.core.config.beans

import com.wits.pms.statuscontrol.PowerManagerApp

class SystemOptions(
    var startAtBoot: Boolean?,
    var hijackCS: Boolean?,
    var soundRestorer: Boolean?,
    var autoTheme: Boolean?,
    var autoVolume: Boolean?,
    var maxVolume: Boolean?,
    var logMcuEvent: Boolean?,
    var interceptMcuCommand: Boolean?,
    var extraMediaButtonHandle: Boolean?,
    var nightBrightness: Boolean?,
    var nightBrightnessLevel: Int?,
    var mcuPath: String?
) {
    companion object {
        fun initSystemTweaks() : SystemOptions {
            return SystemOptions(
                    startAtBoot = false,
                    hijackCS = false,
                    soundRestorer = false,
                    autoTheme = false,
                    autoVolume = false,
                    maxVolume = true,
                    logMcuEvent = true,
                    interceptMcuCommand = true,
                    extraMediaButtonHandle = PowerManagerApp.getSettingsInt("CarDisplay") == 0,
                    nightBrightness = false,
                    nightBrightnessLevel = 65,
                    mcuPath = ""
            )
        }
    }
}