package com.snaggly.ksw_toolkit.core.service.mcu

import android.media.AudioManager
import com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch.IScreenSwitchAction
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.wits.pms.statuscontrol.WitsStatus
import projekt.auto.mcu.ksw.model.McuStatus
import projekt.auto.mcu.ksw.model.SystemStatus
import java.util.*
import kotlin.math.roundToInt

object McuLogic {
    //Lib
    var mcuCommunicator : CustomMcuCommunicator? = null
    val mcuStat = McuStatus()
    var systemStatus: SystemStatus = SystemStatus()
    var screenSwitchActions : LinkedList<IScreenSwitchAction> = LinkedList()

    //Intern
    private var autoVolume = false
    private var senderInterval: Long = 1000
    var isAnyLightOn = false
    var retainVolumes = false
    const val ANDROID_MODE = 1
    const val OEM_MODE = 2

    //Param
    private var speedMaxVolume = 80
    private var minVolume = 0.75f
    var hasInterceptedCarData = false @Synchronized get @Synchronized set
    var actionLock = false @Synchronized get @Synchronized set
    var hasNoOEMScreen = false @Synchronized get @Synchronized set
    var hasBacklightAuto = false @Synchronized get @Synchronized set
    var advancedBrightnessActive = false @Synchronized get @Synchronized set
    var isReversing = false @Synchronized get @Synchronized set
    var turnedOffScreen = false @Synchronized get private set
    var realSysMode : Int = -1 @Synchronized private set

    init {
        mcuStat.systemMode = 1
        WitsStatus.getMcuVersion()?.let { mcuStat.mcuVerison = it }
        WitsStatus.getCarDataStatus()?.let { mcuStat.carData = it }
        WitsStatus.getACDataStatus()?.let { mcuStat.acData = it }
        WitsStatus.getBenzDataStatus()?.let { mcuStat.benzData = it }
        WitsStatus.getSystemStatus()?.let { systemStatus = it }
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

    @Synchronized fun restoreScreenState() {
        for (action in screenSwitchActions)
            action.restoreState()
    }

    @Synchronized fun resetScreenState(backTapper: BackTapper) {
        when (realSysMode) {
            ANDROID_MODE -> {
                if (!turnedOffScreen) {
                    backTapper.removeBackWindow()
                }
                for (action in screenSwitchActions) {
                    action.performOnAndroidSwitch()
                }
            }
            OEM_MODE -> {
                for (action in screenSwitchActions) {
                    action.performOnOEMSwitch()
                }
                backTapper.drawBackWindow()
            }
            else -> {
                backTapper.drawBackWindow()
            }
        }
    }

    @Synchronized fun setRealSysMode(value: Int, backTapper: BackTapper) {
        if (realSysMode == value)
            return

        realSysMode = value
        resetScreenState(backTapper)
    }

    @Synchronized fun setTurnedOffScreen(value: Boolean, backTapper: BackTapper) {
        if (turnedOffScreen != value) {
            turnedOffScreen = value
            if (turnedOffScreen) {
                backTapper.drawBackWindow()
            }
            else {
                backTapper.removeBackWindow()
            }
        }
    }

    fun initSysMode() {
        realSysMode = -1
    }
}