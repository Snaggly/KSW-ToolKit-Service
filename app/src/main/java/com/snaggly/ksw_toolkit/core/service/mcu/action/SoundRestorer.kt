package com.snaggly.ksw_toolkit.core.service.mcu.action

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.enums.SOUND_SRC_TYPE

abstract class SoundRestorer {
    abstract fun reEnableSound()

    object HasSoundRestorer : SoundRestorer() {
        override fun reEnableSound() {
            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SetMusicSource(SOUND_SRC_TYPE.SRC_ATSL_AIRCONSOLE))
        }
    }

    object NoSoundRestorer : SoundRestorer() {
        override fun reEnableSound() { }
    }
}