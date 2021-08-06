package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting
import com.snaggly.ksw_toolkit.core.config.custom.IntegerSetting
import com.wits.pms.statuscontrol.PowerManagerApp

class SystemTweaks(startAtBoot: Boolean,
                   kswService: Boolean,
                   soundRestorer: Boolean,
                   autoTheme: Boolean,
                   autoVolume: Boolean,
                   maxVolume: Boolean,
                   hideTopBar: Boolean,
                   shrinkTopBar: Boolean,
                   logMcuEvent: Boolean,
                   interceptMcuCommand: Boolean,
                   extraMediaButtonHandle: Boolean,
                   nightBrightness: Boolean,
                   nightBrightnessLevel: Int) {
    var startAtBoot = BooleanSetting(startAtBoot)
    var kswService = BooleanSetting(kswService)
    var soundRestorer = BooleanSetting(soundRestorer)
    var autoTheme = BooleanSetting(autoTheme)
    var autoVolume = BooleanSetting(autoVolume)
    var maxVolume = BooleanSetting(maxVolume)
    var hideTopBar = BooleanSetting(hideTopBar)
    var shrinkTopBar = BooleanSetting(shrinkTopBar)
    var logMcuEvent = BooleanSetting(logMcuEvent)
    var interceptMcuCommand = BooleanSetting(interceptMcuCommand)
    var extraMediaButtonHandle = BooleanSetting(extraMediaButtonHandle)
    var nightBrightness = BooleanSetting(nightBrightness)
    var nightBrightnessLevel = IntegerSetting(nightBrightnessLevel)

    companion object {
        fun initSystemTweaks() : SystemTweaks {
            return SystemTweaks(
                    startAtBoot = true,
                    kswService = false,
                    soundRestorer = true,
                    autoTheme = false,
                    autoVolume = false,
                    maxVolume = true,
                    hideTopBar = false,
                    shrinkTopBar = false,
                    logMcuEvent = true,
                    interceptMcuCommand = true,
                    extraMediaButtonHandle = PowerManagerApp.getSettingsInt("CarDisplay") == 0,
                    nightBrightness = false,
                    nightBrightnessLevel = 20
            )
        }
    }
}