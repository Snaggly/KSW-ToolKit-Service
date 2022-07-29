package com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces

import com.snaggly.ksw_toolkit.core.service.mcu.action.SoundRestorer
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
abstract class IScreenSwitchEvent {
    var soundRestorer : SoundRestorer = SoundRestorer.NoSoundRestorer

    abstract fun getScreenSwitch(data: ByteArray) : EventManagerTypes

    protected fun processToAndroid() {
        soundRestorer.reEnableSound()
    }

    fun processToOEM() {
        soundRestorer.reEnableSound()
    }
}