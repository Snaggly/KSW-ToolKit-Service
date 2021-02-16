package com.snaggly.ksw_toolkit.util.commander

import android.content.Context
import android.provider.Settings
import com.snaggly.ksw_toolkit.util.list.mcu.McuCommandsEnum
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.enums.SOUND_SRC_TYPE

object McuCommander {
    fun executeCommand(mcuCommandsEnum: McuCommandsEnum, mcuCommunicator: McuCommunicator?, context: Context) {
        when (mcuCommandsEnum) {
            McuCommandsEnum.ScreenOff -> {
                mcuCommunicator?.sendCommand(McuCommands.SYS_SCREEN_OFF)
            }

            McuCommandsEnum.BrightnessInc -> {
                var currentBrightness = (Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255) * 100) / 255
                currentBrightness += 10
                if (currentBrightness > 100)
                    currentBrightness = 100
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, currentBrightness * 255 / 100)
                mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(currentBrightness.toByte()))
            }

            McuCommandsEnum.BrightnessDec -> {
                var currentBrightness = (Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255) * 100) / 255
                currentBrightness -= 10
                if (currentBrightness < 0)
                    currentBrightness = 0
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, currentBrightness * 255 / 100)
                mcuCommunicator?.sendCommand(McuCommands.SetBrightnessLevel(currentBrightness.toByte()))
            }

            McuCommandsEnum.CarInfo -> mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_OEM)

            McuCommandsEnum.Radio -> mcuCommunicator?.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_RADIO))

            McuCommandsEnum.F_CAM -> mcuCommunicator?.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_FRONTCAM))

            McuCommandsEnum.AUX -> mcuCommunicator?.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_AUX))

            McuCommandsEnum.DVR -> mcuCommunicator?.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_DVR))

            McuCommandsEnum.DVD -> mcuCommunicator?.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_DVD))

            McuCommandsEnum.DTV -> mcuCommunicator?.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_DTV))
        }
    }
}