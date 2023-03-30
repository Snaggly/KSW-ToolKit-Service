package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.ComponentName
import android.content.Intent
import android.util.Log
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.manager.AndroidVolumeManager
import com.wits.pms.statuscontrol.CallBackBinder
import com.wits.pms.statuscontrol.PowerManagerApp
import com.wits.pms.statuscontrol.WitsStatus
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class PowerEvent(private val backTapper: BackTapper) {
    fun getPowerEvent(data: ByteArray, screenSwitchEvent: ScreenSwitchEvent): EventManagerTypes {
        if (data.size <= 1) {
            return EventManagerTypes.Dummy
        }
        //PowerOn
        if (data[0].toInt() == 4 && data[1].toInt() == 1) {
            //Reset NightBrightness
            if (McuLogic.nightBrightness >= 0 && McuLogic.hasBacklightAuto) {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_On)
            }

            //Reset Screen State
            screenSwitchEvent.getScreenSwitch(byteArrayOf(0, McuLogic.realSysMode.toByte()))

            McuLogic.mcuCommunicator?.sendCommand(McuCommands.SYS_SCREEN_ON)
            WitsStatus.setScreenSwitch(McuLogic.systemStatus, McuLogic.realSysMode)
        }
        //PowerOff
        else if (data[0].toInt() == 5) {
            //Restore NightBrightness
            if (McuLogic.nightBrightness >= 0 && McuLogic.hasBacklightAuto) {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.Set_Backlight_Control_Off)
            }

            if (McuLogic.retainVolumes) {
                AndroidVolumeManager(backTapper.context.applicationContext).saveCurrentVolumes()
            }

            //Restore MediaButton
            screenSwitchEvent.restoreState()

            WitsStatus.setAcc(McuLogic.systemStatus, data[1].toInt())
            WitsStatus.setScreenSwitch(McuLogic.systemStatus, McuLogic.realSysMode)
        } else if (data[0] <= 3.toByte()) {
            WitsStatus.setCcd(McuLogic.systemStatus, data[1].toInt())
            if (data[0] == 1.toByte()) {
                McuLogic.isReversing = data[1] == 1.toByte()
                backTapper.drawBackWindow()
                if (PowerManagerApp.getSettingsInt("RearCamType") == 1) {
                    if (McuLogic.isReversing)
                        screenSwitchEvent.getScreenSwitch(byteArrayOf(0, 2))
                    else
                        screenSwitchEvent.getScreenSwitch(byteArrayOf(0, 1))
                }
            } else if (data[0] == 2.toByte()) {
                Log.d("CallBackServiceImpl", "start360");
                val intent = Intent()
                intent.component = ComponentName("com.baony.avm360", "com.baony.ui.service.AVMCanBusService")
                backTapper.context.startService(intent)
                try {
                    Log.d("CallBackServiceImpl", "handleReverse  iCallBack :" + CallBackBinder.getServiceCallbackController().iCallBack + "   ccd = " + McuLogic.systemStatus.ccd)
                    CallBackBinder.handleReverse(McuLogic.systemStatus.ccd)
                } catch (e: Exception) {
                    Log.e("CallBackServiceImpl", "handleReverse  NO iCALLBACK! ccd = ${McuLogic.systemStatus.ccd}")
                }
            }
        }

        return EventManagerTypes.PowerEvent
    }
}