package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting
import com.snaggly.ksw_toolkit.core.config.custom.IntegerSetting

class SystemTweaks(startAtBoot: Boolean,
                   kswService: Boolean,
                   carDataLogging: Boolean,
                   autoTheme: Boolean,
                   autoVolume: Boolean,
                   maxVolume: Boolean,
                   hideTopBar: Boolean,
                   shrinkTopBar: Boolean,
                   dpi: Int,
                   logMcuEvent: Boolean,
                   interceptMcuCommand: Boolean) {
    var startAtBoot = BooleanSetting(startAtBoot)
    var kswService = BooleanSetting(kswService)
    var carDataLogging = BooleanSetting(carDataLogging)
    var autoTheme = BooleanSetting(autoTheme)
    var autoVolume = BooleanSetting(autoVolume)
    var maxVolume = BooleanSetting(maxVolume)
    var hideTopBar = BooleanSetting(hideTopBar)
    var shrinkTopBar = BooleanSetting(shrinkTopBar)
    var dpi = IntegerSetting(dpi)
    var logMcuEvent = BooleanSetting(logMcuEvent)
    var interceptMcuCommand = BooleanSetting(interceptMcuCommand)

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
                    dpi = 160,
                    logMcuEvent = false,
                    interceptMcuCommand = false
            )
        }
    }
}