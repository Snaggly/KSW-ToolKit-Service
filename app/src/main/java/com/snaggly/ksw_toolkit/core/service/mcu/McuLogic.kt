package com.snaggly.ksw_toolkit.core.service.mcu

import android.media.AudioManager
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.wits.pms.statuscontrol.WitsStatus
import projekt.auto.mcu.ksw.model.McuStatus
import projekt.auto.mcu.ksw.serial.McuCommunicator
import kotlin.math.roundToInt

object McuLogic {
    //Lib
    var mcuCommunicator : McuCommunicator? = null
    val mcuStat = McuStatus()

    //Intern
    private var autoVolume = false
    var senderInterval: Long = 1000
    var backTapper : BackTapper? = null

    //Param
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

    fun sendCarData() {
        //WitsStatus.logMcuStatus(mcuStat)
        WitsStatus.sendOutMcuStatus(mcuStat)
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
}