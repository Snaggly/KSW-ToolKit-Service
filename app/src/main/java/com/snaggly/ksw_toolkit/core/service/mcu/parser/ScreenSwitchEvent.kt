package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch.IScreenSwitchAction
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import java.util.LinkedList

class ScreenSwitchEvent(private val backTapper: BackTapper) {
    private var switchActions : LinkedList<IScreenSwitchAction> = LinkedList()

    companion object {
        const val ANDROID_MODE = 1
        const val OEM_MODE = 2
    }

    fun clearActions() {
        restoreState()
        switchActions.clear()
    }

    fun addAction(action: IScreenSwitchAction) {
        switchActions.add(action)
    }

    fun getScreenSwitch(data: ByteArray) : EventManagerTypes {
        if (data.size < 2)
            return EventManagerTypes.Dummy

        val mode = data[1].toInt()
        McuLogic.setRealSysMode(mode, backTapper)

        if (mode == ANDROID_MODE) {
            for (action in switchActions)
                action.performOnAndroidSwitch()
        }
        else if (mode == OEM_MODE) {
            for (action in switchActions)
                action.performOnOEMSwitch()
        }

        return EventManagerTypes.ScreenSwitch
    }

    fun restoreState() {
        for (action in switchActions)
            action.restoreState()
    }
}