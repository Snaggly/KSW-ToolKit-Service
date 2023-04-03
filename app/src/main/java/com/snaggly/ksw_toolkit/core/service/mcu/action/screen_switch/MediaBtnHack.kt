package com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch

import android.util.Log
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class MediaBtnHack : IScreenSwitchAction {
    private val enableOEM = byteArrayOf(0x0e, 0x01)
    private val disableOEM = byteArrayOf(0x0e, 0x00)
    private val oemCAMType = byteArrayOf(0x0b, 0x01)
    private val spoofedCAMType = byteArrayOf(0x0b, 0x04)
    private var switchLock = false @Synchronized get

    override fun performOnAndroidSwitch() {
        switchLock = false
        McuLogic.mcuCommunicator?.sendCommand(0x70, disableOEM, false)

        /* Unfortunately this does not work; MCU fully restricts any screen switches when in reverse
        if (PowerManagerApp.getSettingsInt("RearCamType") == 1) //When in Android switch to a detectable type to be able to perform switch
            McuLogic.mcuCommunicator?.sendCommand(0x6A, spoofedCAMType, false)
        */
    }

    override fun performOnOEMSwitch() {
        if (!switchLock) {
            switchLock = true
            /* See comment above
            if (PowerManagerApp.getSettingsInt("RearCamType") == 1)
                McuLogic.mcuCommunicator?.sendCommand(0x6A, oemCAMType, false)*/
            McuLogic.mcuCommunicator?.sendCommand(0x70, enableOEM, false)
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_OEM) //Already in Mode2, force second switch take place
        }
    }

    override fun restoreState() {
        /* See comment above
        if (PowerManagerApp.getSettingsInt("RearCamType") == 1)
            McuLogic.mcuCommunicator?.sendCommand(0x6A, oemCAMType, false)*/
        if (PowerManagerApp.getSettingsInt("CarDisplay") > 0)
            McuLogic.mcuCommunicator?.sendCommand(0x70, enableOEM, false)
    }
}