package com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.enums.SOUND_SRC_TYPE

class SoundRestorer : IScreenSwitchAction {
    override fun performOnAndroidSwitch() {
        reEnableSound()
    }

    override fun performOnOEMSwitch() {
        reEnableSound()
    }

    override fun restoreState() {

    }

    private fun reEnableSound() {
        McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_ATSL_AIRCONSOLE))
    }
}