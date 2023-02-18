package com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch

import android.util.Log
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.wits.pms.statuscontrol.PowerManagerApp
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class MediaBtnHack : IScreenSwitchAction {
    private val enableOEM = byteArrayOf(0x0e, 0x01)
    private val disableOEM = byteArrayOf(0x0e, 0x00)
    private var switchLock = false @Synchronized get

    override fun performOnAndroidSwitch() {
        switchLock = false
        McuLogic.mcuCommunicator?.sendCommand(0x70, disableOEM, false)
    }

    override fun performOnOEMSwitch() {
        if (!switchLock) {
            switchLock = true
            McuLogic.mcuCommunicator?.sendCommand(0x70, enableOEM, false)
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_OEM)
        }
    }

    override fun restoreState() {
        if (PowerManagerApp.getSettingsInt("CarDisplay") > 0)
            McuLogic.mcuCommunicator?.sendCommand(0x70, enableOEM, false)
    }
}