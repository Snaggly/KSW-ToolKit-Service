package com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class NormalScreenSwitch : IScreenSwitchAction {
    override fun performOnAndroidSwitch() {

    }

    override fun performOnOEMSwitch() {

    }

    override fun restoreState() {
        if (McuLogic.realSysMode == 2)
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_OEM)
        else
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_ANDROID)
    }
}