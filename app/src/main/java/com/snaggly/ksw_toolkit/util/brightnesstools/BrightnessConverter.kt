package com.snaggly.ksw_toolkit.util.brightnesstools

import com.wits.pms.utils.BrightnessUtils
import kotlin.math.roundToInt

object BrightnessConverter {
    fun convertAndroidUnitToPercent(value: Int) : Int {
        return (100.0 * BrightnessUtils.getPercentage(BrightnessUtils.convertLinearToGamma(value, 10, 255).toDouble(), 0, 1023)).roundToInt()
    }

    fun convertPercentToAndroidUnit(value: Int) : Int {
        return BrightnessUtils.convertGammaToLinear((value * 1023) / 100, 10, 255)
    }
}