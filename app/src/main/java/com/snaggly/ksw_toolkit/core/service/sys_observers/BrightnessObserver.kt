package com.snaggly.ksw_toolkit.core.service.sys_observers

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import android.util.Log
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class BrightnessObserver(private val context: Context) {
    private val brightnessObserver = object : ContentObserver(Handler(context.mainLooper)) {
        override fun onChange(selfChange: Boolean) {
            val newBrightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel((newBrightness * 100 / 255).toByte()))
            Log.d("KSWToolKit", "D 3:" + (newBrightness * 100 / 255).toString() + " / " + Settings.System.SCREEN_BRIGHTNESS)
        }
    }

    fun startObservingBrightness() {
        context.contentResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, brightnessObserver)
    }

    fun stopObservingBrightness() {
        context.contentResolver.unregisterContentObserver(brightnessObserver)
    }

}