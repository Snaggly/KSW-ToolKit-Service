package com.snaggly.ksw_toolkit.core.service.sys_observers

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import com.snaggly.ksw_toolkit.core.service.mcu.McuEventLogicImpl
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class NaviAppObserver(private val context: Context, mcuLogic: McuEventLogicImpl) {

    private val naviObserver = object : ContentObserver(Handler(context.mainLooper)) {
        override fun onChange(selfChanged: Boolean) {
            val result = Settings.System.getInt(context.contentResolver, "usingNavi")
            val isUsingNavi = Settings.System.getInt(context.contentResolver, "usingNavi") == 1
            if (isUsingNavi){
                mcuLogic.mcuCommunicator?.sendCommand(McuCommands.Start_Voice)
            }
            else {
                mcuLogic.mcuCommunicator?.sendCommand(McuCommands.Stop_Voice)
            }
        }
    }

    fun startHandlingNaviCallouts() {
        context.contentResolver.registerContentObserver(Settings.System.getUriFor("usingNavi"), true, naviObserver)
    }

    fun stopHandlingNaviCallouts() {
        context.contentResolver.unregisterContentObserver(naviObserver)
    }

}