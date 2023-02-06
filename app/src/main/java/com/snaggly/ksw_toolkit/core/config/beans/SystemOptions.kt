package com.snaggly.ksw_toolkit.core.config.beans

class SystemOptions(
    var startAtBoot: Boolean? = false,
    var hijackCS: Boolean? = false,
    var soundRestorer: Boolean? = false,
    var autoTheme: Boolean? = false,
    var autoVolume: Boolean? = false,
    var maxVolume: Boolean? = false,
    var logMcuEvent: Boolean? = true,
    var interceptMcuCommand: Boolean? = true,
    var extraMediaButtonHandle: Boolean? = false,
    var nightBrightness: Boolean? = false,
    var nightBrightnessLevel: Int? = 65,
    var mcuPath: String? = "",
    var tabletMode: Boolean? = false,
    var hideStartMessage: Boolean? = false,
    var decoupleNAVBtn: Boolean?
) {
    companion object {
        fun initSystemTweaks() : SystemOptions {
            return SystemOptions(
                    startAtBoot = false,
                    hijackCS = false,
                    soundRestorer = false,
                    autoTheme = false,
                    autoVolume = false,
                    maxVolume = false,
                    logMcuEvent = true,
                    interceptMcuCommand = true,
                    extraMediaButtonHandle = false,
                    nightBrightness = false,
                    nightBrightnessLevel = 65,
                    mcuPath = "",
                    tabletMode = false,
                    hideStartMessage = false,
                    decoupleNAVBtn = false
            )
        }
    }
}