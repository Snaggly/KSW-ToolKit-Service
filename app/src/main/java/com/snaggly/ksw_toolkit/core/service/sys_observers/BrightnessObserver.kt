package com.snaggly.ksw_toolkit.core.service.sys_observers

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.wits.pms.utils.BrightnessUtils
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import kotlin.math.roundToInt

class BrightnessObserver(private val context: Context) {

    private val brightnessObserver = object : ContentObserver(Handler(context.mainLooper)) {
        override fun onChange(selfChange: Boolean) {
            val newBrightness = (100.0 * BrightnessUtils.getPercentage(
                BrightnessUtils.convertLinearToGamma(
                    Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS), 10, 255
                ).toDouble(), 0, 1023
            )).roundToInt()
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(newBrightness.toByte()))
        }
    }

    fun startObservingBrightness() {
        context.contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
            true,
            brightnessObserver
        )
    }

    fun stopObservingBrightness() {
        context.contentResolver.unregisterContentObserver(brightnessObserver)
    }

}