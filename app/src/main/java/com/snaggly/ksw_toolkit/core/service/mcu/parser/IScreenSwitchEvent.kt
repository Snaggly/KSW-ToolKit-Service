package com.snaggly.ksw_toolkit.core.service.mcu.parser

import com.snaggly.ksw_toolkit.util.list.eventtype.EventManagerTypes

interface IScreenSwitchEvent {
    fun getScreenSwitch(data: ByteArray) : EventManagerTypes

    var hasSoundRestorer : Boolean
}