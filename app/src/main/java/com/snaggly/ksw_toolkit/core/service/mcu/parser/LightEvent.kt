package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.ILightEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.utils.BrightnessUtils
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import kotlin.experimental.and
import kotlin.math.roundToInt


object LightEvent : ILightEvent {
    override fun getCarDataEvent(data: ByteArray, context: Context): EventManagerTypes {

        if (McuLogic.nightBrightness >= 0) {
            if (data[1].and(7) > 1) {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(McuLogic.nightBrightness.toByte()))
            } else {
                val newBrightness = (100.0 * BrightnessUtils.getPercentage(
                    BrightnessUtils.convertLinearToGamma(
                        Settings.System.getInt(
                            context.contentResolver, Settings.System.SCREEN_BRIGHTNESS
                        ), 10, 255
                    ).toDouble(), 0, 1023
                )).roundToInt()
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(newBrightness.toByte()))
            }
        }


        return EventManagerTypes.CarData
    }
}