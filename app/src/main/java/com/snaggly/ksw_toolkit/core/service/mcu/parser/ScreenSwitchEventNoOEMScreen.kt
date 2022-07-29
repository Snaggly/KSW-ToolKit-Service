package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.parser.interfaces.IScreenSwitchEvent
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.commander.KeyInjector
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import com.snaggly.ksw_toolkit.util.list.keyevent.KeyCode

class ScreenSwitchEventNoOEMScreen(private val context: Context, private val backTapper: BackTapper) : IScreenSwitchEvent() {
    override fun getScreenSwitch(data: ByteArray): EventManagerTypes {

        McuLogic.setRealSysMode(data[1].toInt(), backTapper)
        if (McuLogic.realSysMode == 0x1) {
            super.processToAndroid()
            KeyInjector.sendKey(KeyCode.HOME.keycode)
        } else if (McuLogic.realSysMode == 0x2) {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component = ComponentName("com.wits.pms", "com.wits.pms.ClockActivity")
            context.startActivity(intent)
            super.processToOEM()
        }

        return EventManagerTypes.ScreenSwitch
    }
}