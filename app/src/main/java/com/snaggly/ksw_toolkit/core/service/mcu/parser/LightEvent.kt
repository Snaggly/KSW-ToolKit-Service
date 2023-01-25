package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import android.provider.Settings
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.ILightEvent
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.wits.pms.utils.BrightnessUtils
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import kotlin.experimental.and
import kotlin.math.roundToInt


class LightEvent(private val context: Context) : ILightEvent {
    override fun getCarDataEvent(data: ByteArray): EventManagerTypes {

        if (McuLogic.nightBrightness >= 0) {
            if (data[1].and(7) >= 1) {  // Maybe bit2 for parking lights, bit0 for headlights
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(McuLogic.nightBrightness.toByte()))
                McuLogic.isAnyLightOn = true
            } else {
                val newBrightness = (100.0 * BrightnessUtils.getPercentage(
                    BrightnessUtils.convertLinearToGamma(
                        Settings.System.getInt(
                            context.contentResolver, Settings.System.SCREEN_BRIGHTNESS
                        ), 10, 255
                    ).toDouble(), 0, 1023
                )).roundToInt()
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(newBrightness.toByte()))
                McuLogic.isAnyLightOn = false
            }
        }

        return EventManagerTypes.CarData
    }
}