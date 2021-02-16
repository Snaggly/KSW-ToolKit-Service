package com.snaggly.ksw_toolkit.core.service.sys_observers

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class NaviAppObserver(private val context: Context) {

    private val naviObserver = object : ContentObserver(Handler(context.mainLooper)) {
        override fun onChange(selfChanged: Boolean) {
            val isUsingNavi = Settings.System.getInt(context.contentResolver, "usingNavi") == 1
            if (isUsingNavi){
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.Start_Voice)
            }
            else {
                McuLogic.mcuCommunicator?.sendCommand(McuCommands.Stop_Voice)
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