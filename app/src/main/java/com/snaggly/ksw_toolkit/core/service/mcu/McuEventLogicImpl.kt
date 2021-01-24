package com.snaggly.ksw_toolkit.core.service.mcu

import projekt.auto.mcu.ksw.serial.collection.McuEvent
import projekt.auto.mcu.ksw.serial.collection.McuEventLogic

class McuEventLogicImpl : McuEventLogic {
    override fun getMcuEvent(cmdType: Int, data: ByteArray) : McuEvent? {
        if (cmdType == 0x1C) {
            if (data[0] == 0x1.toByte()) return McuEvent.Idle
        }
        else if (cmdType == 0xA1) {
            if (data[0] == 0x17.toByte()) {
                if (data[1] == 0x6.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveKnobTurnClockwise
                if (data[1] == 0x7.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveKnobTurnCounterClockwise
                if (data[1] == 0x2.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveKnobTiltDown
                if (data[1] == 0x3.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveKnobTiltLeft
                if (data[1] == 0x4.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveKnobTiltRight
                if (data[1] == 0x1.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveKnobTiltUp
                if (data[1] == 0x5.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveKnobPressed
                if (data[1] == 0x19.toByte() && data[2] == 0x0.toByte()) return McuEvent.MediaPlayPauseButtonPressed
                if (data[1] == 0x18.toByte() && data[2] == 0x0.toByte()) return McuEvent.MediaNextButtonPressed
                if (data[1] == 0x17.toByte() && data[2] == 0x0.toByte()) return McuEvent.MediaPreviousButtonPressed
                if (data[1] == 0x14.toByte() && data[2] == 0x1.toByte()) return McuEvent.VoiceCommandPress
                if (data[1] == 0x8.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveMenuButtonPressed
                if (data[1] == 0xc.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveBackButtonPress
                if (data[1] == 0xe.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveNavigationButtonPressed
                if (data[1] == 0xd.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveOptionsButtonPress
                if (data[1] == 0x11.toByte() && data[2] == 0x1.toByte()) return McuEvent.SteeringWheelTelButtonPressed
                if (data[1] == 0xb.toByte() && data[2] == 0x1.toByte()) return McuEvent.iDriveTelephoneButtonLongPress
                if (data[1] == 0x10.toByte() && data[2] == 0x1.toByte()) return McuEvent.MenuButtonPress
            } else if (data[0] == 0x1A.toByte()) {
                if (data[1] == 0x1.toByte()) return McuEvent.SWITCHED_TO_ARM
                if (data[1] == 0x2.toByte()) return McuEvent.SWITCHED_TO_OEM
            }
        }

        return null
    }
}