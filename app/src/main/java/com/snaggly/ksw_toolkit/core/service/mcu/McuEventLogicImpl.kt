package com.snaggly.ksw_toolkit.core.service.mcu

import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus
import projekt.auto.mcu.ksw.model.McuStatus
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import projekt.auto.mcu.ksw.serial.collection.McuEvent

class McuEventLogicImpl {
    //Lib
    var mcuCommunicator : McuCommunicator? = null
    private val mcuStat = McuStatus()

    //Intern
    private var isLogging = true
    var senderInterval: Long = 1000

    //Param
    var hasNoOEMScreen = false

    init {
        try{
            mcuStat.carData = WitsStatus.getCarDataStatus()
            mcuStat.acData = WitsStatus.getACDataStatus()
            mcuStat.benzData = WitsStatus.getBenzDataStatus()
        }
        catch (exc : Exception) {}
    }

    fun startSendingCarData() {
        isLogging = true
        Thread {
            while (isLogging) {
                //WitsStatus.sendOutMcuStatus(mcuStat) CS overwrites immediately!
                WitsStatus.logMcuStatus(mcuStat)
                Thread.sleep(senderInterval)
            }
        }.start()
    }

    fun stopSendingCarData() {
        isLogging = false
    }

    fun getMcuEvent(cmdType: Int, data: ByteArray): EventManagerTypes? {
        if (cmdType == 0xA1) {
            if (data[0] == 0x17.toByte()) {
                if (data[1] == 0x6.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTurnRight
                if (data[1] == 0x7.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTurnLeft
                if (data[1] == 0x2.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTiltDown
                if (data[1] == 0x3.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTiltLeft
                if (data[1] == 0x4.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTiltRight
                if (data[1] == 0x1.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobTiltUp
                if (data[1] == 0x5.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.KnobPress
                if (data[1] == 0x19.toByte() && data[2] == 0x0.toByte()) return EventManagerTypes.MediaPlayPause
                if (data[1] == 0x18.toByte() && data[2] == 0x0.toByte()) return EventManagerTypes.MediaNext
                if (data[1] == 0x17.toByte() && data[2] == 0x0.toByte()) return EventManagerTypes.MediaPrevious
                if (data[1] == 0x14.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.VoiceCommandButton
                if (data[1] == 0x10.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.ModeButton
                if (data[1] == 0x8.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.MenuButton
                if (data[1] == 0xc.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.BackButton
                if (data[1] == 0xe.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.NavigationButton
                if (data[1] == 0xd.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.OptionsButton
                if (data[1] == 0x11.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.TelephoneButton
                if (data[1] == 0xb.toByte() && data[2] == 0x1.toByte()) return EventManagerTypes.TelephoneButtonLongPress
            } else if (data[0] == 0x1A.toByte() && hasNoOEMScreen) {
                if (data[1] == 0x1.toByte()) {
                    mcuCommunicator!!.sendCommand(McuCommands.SYS_SCREEN_ON)
                } else if (data[1] == 0x2.toByte()) {
                    mcuCommunicator!!.sendCommand(McuCommands.SYS_SCREEN_OFF)
                }
            } else if (data[0] == 0x19.toByte()) {
                mcuStat.carData.parseFromCarDataEvent(data)
            }  else if (data[0] == 0x10.toByte()) {
                mcuStat.carData.parseFromBrakeBeltEvent(data)
            } else if (data[0] == 0x12.toByte()) {
                mcuStat.carData.parseFromDoorEvent(data)
            } else if (data[0] == 0x1C.toByte()) {
                mcuStat.acData.parseFromACDataEvent(data)
            }
            return EventManagerTypes.CarData
        }
        else if (cmdType == 0x1D) {
            mcuStat.benzData.parseFromBenzDataEvent(data)
            return EventManagerTypes.BenzData
        }
        else if (cmdType == 0x1C) {
            if (data[0] == 0x1.toByte()) return EventManagerTypes.Idle
        }

        return null
    }
}