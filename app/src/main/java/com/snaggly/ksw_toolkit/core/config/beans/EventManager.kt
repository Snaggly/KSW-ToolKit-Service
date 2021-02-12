package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.custom.IntegerSetting
import com.snaggly.ksw_toolkit.core.config.custom.StringSetting
import com.snaggly.ksw_toolkit.util.enums.EventMode

class EventManager(eventMode: EventMode, keyCode: Int, appName: String, mcuCommandMode: Int){

    var eventMode = eventMode
    var keyCode = IntegerSetting(keyCode)
    var appName = StringSetting(appName)
    var mcuCommandMode = IntegerSetting(mcuCommandMode)

    companion object {
        fun initEventManager() : EventManager{
            return EventManager(EventMode.NoAssignment, -1, "", -1)
        }
    }
}
