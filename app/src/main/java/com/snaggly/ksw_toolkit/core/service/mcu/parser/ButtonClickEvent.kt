package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

object ButtonClickEvent {
    fun getClickEvent(data: ByteArray) : EventManagerTypes {
        if (data[1] == 0x6.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTurnRight
        if (data[1] == 0x7.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTurnLeft
        if (data[1] == 0x2.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTiltDown
        if (data[1] == 0x3.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTiltLeft
        if (data[1] == 0x4.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTiltRight
        if (data[1] == 0x1.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTiltUp
        if (data[1] == 0x5.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobPress
        if (data[1] == 0x19.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.MediaPlayPause
        if (data[1] == 0x18.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.MediaNext
        if (data[1] == 0x17.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.MediaPrevious
        if (data[1] == 0x16.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.VolumeDecrease
        if (data[1] == 0x15.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.VolumeIncrease
        if (data[1] == 0x14.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.VoiceCommandButton
        if (data[1] == 0x10.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.ModeButton
        if (data[1] == 0x11.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.TelephoneButton
        if (data[1] == 0x1f.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.TelephoneButtonHangUp
        if (data[1] == 0xb.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.TelephoneButtonLongPress
        if (data[1] == 0x8.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.MenuButton
        if (data[1] == 0xc.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.BackButton
        if (data[1] == 0xe.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.NavigationButton
        if (data[1] == 0xd.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.OptionsButton
        return EventManagerTypes.CarData
    }
}