package com.snaggly.ksw_toolkit.core.service.mcu.parser

import android.content.Context
import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

abstract class IMcuEvent(context: Context) {
    var benzDataEvent: IBenzDataEvent = BenzDataEvent
    var carDataEvent: ICarDataEvent = CarDataEvent
    var screenSwitchEvent: IScreenSwitchEvent = ScreenSwitchEvent
    val buttonClickEvent = ButtonClickEvent
    val idleEvent = IdleEvent(context)
    abstract fun getMcuEvent(cmdType: Int, data: ByteArray): EventManagerTypes?
}