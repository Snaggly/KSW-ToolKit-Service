package com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.util.commander.KeyInjector
import com.snaggly.ksw_toolkit.util.list.keyevent.KeyCode
import projekt.auto.mcu.ksw.serial.collection.McuCommands

class NoOEMScreen(private val context: Context) : IScreenSwitchAction {
    override fun performOnAndroidSwitch() {
        KeyInjector.sendKey(KeyCode.HOME.keycode)
    }

    override fun performOnOEMSwitch() {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.component = ComponentName("com.wits.pms", "com.wits.pms.ClockActivity")
        context.startActivity(intent)
    }

    override fun restoreState() {
        McuLogic.mcuCommunicator?.sendCommand(McuCommands.SWITCH_TO_ANDROID)
    }
}