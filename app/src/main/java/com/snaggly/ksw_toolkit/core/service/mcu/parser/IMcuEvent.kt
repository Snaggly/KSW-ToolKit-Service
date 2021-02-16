package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

abstract class IMcuEvent(var screenSwitchEvent: IScreenSwitchEvent, var carDataEvent: ICarDataEvent, var benzDataEvent: IBenzDataEvent) {
    val buttonClickEvent = ButtonClickEvent
    val idleEvent = IdleEvent
    abstract fun getMcuEvent(cmdType: Int, data: ByteArray): EventManagerTypes?
}