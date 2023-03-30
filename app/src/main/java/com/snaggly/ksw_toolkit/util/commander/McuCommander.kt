package com.snaggly.ksw_toolkit.util.commander

import android.content.Context
import android.provider.Settings
import com.snaggly.ksw_toolkit.util.list.mcu.McuCommandsEnum
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.enums.SOUND_SRC_TYPE

object McuCommander {
    fun executeCommand(mcuCommandsEnum: McuCommandsEnum, mcuCommunicator: McuCommunicator?, context: Context) {
        when (mcuCommandsEnum) {
            McuCommandsEnum.ScreenOff -> {
                mcuCommunicator?.sendCommand(McuCommands.SYS_SCREEN_OFF)
            }

            McuCommandsEnum.MediaVolumeInc -> {
                var armMediaVol = PowerManagerApp.getManager().getSettingsInt("Android_media_vol") + 1
                if (armMediaVol > 40)
                    armMediaVol = 40
                PowerManagerApp.getManager().setSettingsInt("Android_media_vol", armMediaVol)
            }

            McuCommandsEnum.MediaVolumeDec -> {
                var armMediaVol = PowerManagerApp.getManager().getSettingsInt("Android_media_vol") - 1
                if (armMediaVol < 0)
                    armMediaVol = 0
                PowerManagerApp.getManager().setSettingsInt("Android_media_vol", armMediaVol)
            }

            McuCommandsEnum.BrightnessInc -> {
                var currentBrightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0xFF)
                currentBrightness = (currentBrightness * 1.5).toInt()
                if (currentBrightness > 0xFF)
                    currentBrightness = 0xFF
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, currentBrightness)
            }

            McuCommandsEnum.BrightnessDec -> {
                var currentBrightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0xFF)
                currentBrightness = (currentBrightness / 1.5).toInt()
                if (currentBrightness < 10)
                    currentBrightness = 10
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, currentBrightness)
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