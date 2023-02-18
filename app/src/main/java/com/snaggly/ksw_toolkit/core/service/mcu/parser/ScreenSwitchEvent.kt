package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.core.service.mcu.McuLogic
import com.snaggly.ksw_toolkit.core.service.mcu.action.screen_switch.IScreenSwitchAction
import com.snaggly.ksw_toolkit.core.service.view.BackTapper
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes
import java.util.LinkedList

class ScreenSwitchEvent(private val backTapper: BackTapper) {
    private var switchActions : LinkedList<IScreenSwitchAction> = LinkedList()

    fun clearActions() {
        restoreState()
        switchActions.clear()
    }

    fun addAction(action: IScreenSwitchAction) {
        switchActions.add(action)
    }

    fun getScreenSwitch(data: ByteArray) : EventManagerTypes {
        McuLogic.setRealSysMode(data[1].toInt(), backTapper)
        if (McuLogic.realSysMode == 0x1) {
            for (action in switchActions)
                action.performOnAndroidSwitch()
        }
        else if (McuLogic.realSysMode == 0x2) {
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