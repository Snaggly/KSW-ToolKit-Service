package com.snaggly.ksw_toolkit.gui.viewmodels

import android.content.Context
import android.provider.Settings
import com.snaggly.ksw_toolkit.core.service.helper.CoreServiceClient
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.enums.SOUND_SRC_TYPE

class ToolKitViewModel : CoreServiceClient() {

    fun getBrightness(context: Context) : Int {
        return (Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 255)* 100) / 255
    }

    fun setBrightness(context: Context, brightness: Int) {
        Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness*255/100)
        coreService?.mcuLogic?.mcuCommunicator!!.sendCommand(McuCommands.SetBrightnessLevel(brightness.toByte()))
    }

    fun openOEMScreen() {
        coreService?.mcuLogic?.mcuCommunicator!!.sendCommand(McuCommands.SWITCH_TO_OEM)
    }

    fun openRadioScreen() {
        coreService?.mcuLogic?.mcuCommunicator!!.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_RADIO))
    }

    fun openFCamScreen() {
        coreService?.mcuLogic?.mcuCommunicator!!.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_FRONTCAM))
    }

    fun openAuxScreen() {
        coreService?.mcuLogic?.mcuCommunicator!!.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_AUX))
    }

    fun openDvrScreen() {
        coreService?.mcuLogic?.mcuCommunicator!!.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_DVR))
    }

    fun openDvdScreen() {
        coreService?.mcuLogic?.mcuCommunicator!!.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_DVD))
    }

    fun openDtvScreen() {
        coreService?.mcuLogic?.mcuCommunicator!!.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_DTV))
    }

    fun closeScreen() {
        coreService?.mcuLogic?.mcuCommunicator!!.sendCommand(McuCommands.SYS_SCREEN_OFF)
    }

}