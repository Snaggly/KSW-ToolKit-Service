package com.snaggly.ksw_toolkit.core.service.mcu

import android.media.AudioManager
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes
import com.wits.pms.statuscontrol.WitsStatus
import projekt.auto.mcu.ksw.model.McuStatus
import projekt.auto.mcu.ksw.serial.McuCommunicator
import projekt.auto.mcu.ksw.serial.collection.McuCommands
import kotlin.math.roundToInt

class McuEventLogicImpl {
    //Lib
    var mcuCommunicator : McuCommunicator? = null
    private val mcuStat = McuStatus()

    //Intern
    private var isLogging = true
    private var autoVolume = false
    var senderInterval: Long = 1000
    var backTapper : BackTapper? = null

    //Param
    var hasNoOEMScreen = false
    var speedMaxVolume = 80
    var minVolume = 0.75f

    init {
        try{
            mcuStat.systemMode = 1
            mcuStat.mcuVerison = WitsStatus.getMcuVersion()
            mcuStat.carData = WitsStatus.getCarDataStatus()
            mcuStat.acData = WitsStatus.getACDataStatus()
            mcuStat.benzData = WitsStatus.getBenzDataStatus()
        }
        catch (exc : Exception) {}
    }

    private fun sendCarData() {
        WitsStatus.logMcuStatus(mcuStat)
    }

    fun startSendingCarData() {
        isLogging = true
    }

    fun stopSendingCarData() {
        isLogging = false
    }

    fun startAutoVolume(audioManager: AudioManager) {
        if (autoVolume)
            return
        val minVol = audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volDiffTo = (minVol+maxVol)*minVolume
        val volDiffLe = (minVol+maxVol)*(1-minVolume) / speedMaxVolume
        autoVolume = true
        Thread {
            while(autoVolume) {
                val speedRate = (mcuStat.carData.speed*volDiffLe + volDiffTo).roundToInt().coerceIn(minVol, maxVol)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, speedRate, 0)
                Thread.sleep(senderInterval)
            }
        }.start()
    }

    fun stopAutoVolume() {
        autoVolume = false
    }

    fun getMcuEvent(cmdType: Int, data: ByteArray): EventManagerTypes? {
        if (cmdType == 0xA1) {
            if (data[0] == 0x17.toByte() && data.size > 2) {
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
            } else if (hasNoOEMScreen && data.size > 1 && data[0] == 0x1A.toByte()) {
                if (data[1] == 0x1.toByte()) {
                    mcuCommunicator!!.sendCommand(McuCommands.SYS_SCREEN_ON)
                } else if (data[1] == 0x2.toByte()) {
                    mcuCommunicator!!.sendCommand(McuCommands.SYS_SCREEN_OFF)
                }
            } else if (isLogging) {
                when {
                    data[0] == 0x19.toByte() -> {
                        mcuStat.carData.parseFromCarDataEvent(data)
                    }
                    data[0] == 0x10.toByte() -> {
                        mcuStat.carData.parseFromBrakeBeltEvent(data)
                    }
                    data[0] == 0x12.toByte() -> {
                        mcuStat.carData.parseFromDoorEvent(data)
                    }
                    data[0] == 0x1C.toByte() -> {
                        mcuStat.acData.parseFromACDataEvent(data)
                    }
                }

                sendCarData()
            }
            return EventManagerTypes.CarData
        }
        else if (cmdType == 0x1D) {
            if (isLogging) {
                mcuStat.benzData.parseFromBenzDataEvent(data)
                sendCarData()
            }
            return EventManagerTypes.BenzData
        }
        else if (cmdType == 0x1C) {
            if (data[0] == 0x1.toByte()) {
                backTapper?.removeBackWindow()
                return EventManagerTypes.Idle
            }
            else if (data[0] == 0x2.toByte()) {
                backTapper?.drawBackWindow(mcuCommunicator!!)
                return EventManagerTypes.Idle
            }
        }

        return null
    }
}