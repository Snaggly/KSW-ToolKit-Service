package com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.wits.pms.statuscontrol.PowerManagerApp

class NAVBtnDecoupler : IScreenSwitchAction {
    private val androidNavBtn = byteArrayOf(21, 0)
    private val oemNavBtn = byteArrayOf(21, 1)
    override fun performOnAndroidSwitch() {
        McuLogic.mcuCommunicator?.sendCommand(112, androidNavBtn, false)
    }

    override fun performOnOEMSwitch() {
        McuLogic.mcuCommunicator?.sendCommand(112, oemNavBtn, false)
    }

    override fun restoreState() {
        var originalMode = PowerManagerApp.getSettingsInt("Map_key").toByte()
        if (originalMode < 0)
            originalMode = 0

        McuLogic.mcuCommunicator?.sendCommand(112, byteArrayOf(21, originalMode), false)
    }
}