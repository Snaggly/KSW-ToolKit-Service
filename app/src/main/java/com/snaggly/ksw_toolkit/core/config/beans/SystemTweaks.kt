package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting
import com.wits.pms.statuscontrol.PowerManagerApp

class SystemTweaks(startAtBoot: Boolean,
                   kswService: Boolean,
                   carDataLogging: Boolean,
                   autoTheme: Boolean,
                   autoVolume: Boolean,
                   maxVolume: Boolean,
                   hideTopBar: Boolean,
                   shrinkTopBar: Boolean,
                   logMcuEvent: Boolean,
                   interceptMcuCommand: Boolean,
                   extraMediaButtonHandle: Boolean) {
    var startAtBoot = BooleanSetting(startAtBoot)
    var kswService = BooleanSetting(kswService)
    var carDataLogging = BooleanSetting(carDataLogging)
    var autoTheme = BooleanSetting(autoTheme)
    var autoVolume = BooleanSetting(autoVolume)
    var maxVolume = BooleanSetting(maxVolume)
    var hideTopBar = BooleanSetting(hideTopBar)
    var shrinkTopBar = BooleanSetting(shrinkTopBar)
    var logMcuEvent = BooleanSetting(logMcuEvent)
    var interceptMcuCommand = BooleanSetting(interceptMcuCommand)
    var extraMediaButtonHandle = BooleanSetting(extraMediaButtonHandle)

    companion object {
        fun initSystemTweaks() : SystemTweaks {
            return SystemTweaks(
                    startAtBoot = true,
                    kswService = true,
                    carDataLogging = false,
                    autoTheme = false,
                    autoVolume = false,
                    maxVolume = false,
                    hideTopBar = false,
                    shrinkTopBar = false,
                    logMcuEvent = false,
                    interceptMcuCommand = false,
                    extraMediaButtonHandle = PowerManagerApp.getSettingsInt("CarDisplay") == 0
            )
        }
    }
}