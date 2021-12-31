package com.snaggly.ksw_toolkit.core.service.mcu

import android.media.AudioManager
import com.wits.pms.statuscontrol.WitsStatus
import projekt.auto.mcu.ksw.model.McuStatus
import projekt.auto.mcu.ksw.serial.McuCommunicator
import kotlin.math.roundToInt

object McuLogic {
    //Lib
    var mcuCommunicator : McuCommunicator? = null
    var realSysMode : Int = 1
    val mcuStat = McuStatus()

    //Intern
    var hasInterceptedCarData = false
    private var autoVolume = false
    private var senderInterval: Long = 1000

    //Param
    private var speedMaxVolume = 80
    private var minVolume = 0.75f
    var actionLock = false

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
        hasInterceptedCarData = true
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