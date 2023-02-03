package com.snaggly.ksw_toolkit.core.config.beans

class AdvancedBrightness (
    var isTimeBasedEnabled: Boolean?,
    var isUSBBasedEnabled: Boolean?,
    var sunriseAt: String?,
    var sunsetAt: String?,
    var autoTimes: Boolean?,
    var daylightBrightness: Int?,
    var daylightHLBrightness: Int?,
    var nightBrightnessLevel: Int?,
    var nightHLBrightnessLevel: Int?
) {
    companion object {
        fun initDefault(): AdvancedBrightness {
            return AdvancedBrightness(
                isTimeBasedEnabled = false,
                isUSBBasedEnabled = false,
                sunriseAt = "06:00",
                sunsetAt = "20:00",
                autoTimes = true,
                daylightBrightness = 100,
                daylightHLBrightness = 85,
                nightBrightnessLevel = 50,
                nightHLBrightnessLevel = 30
            )
        }
    }
}