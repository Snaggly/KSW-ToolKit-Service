package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.IConfigBean
import com.snaggly.ksw_toolkit.core.config.custom.IntegerSetting
import com.snaggly.ksw_toolkit.core.config.custom.StringSetting
import com.snaggly.ksw_toolkit.util.enums.EventMode

class EventManager(eventMode: EventMode, keyCode: Int, appName: String, mcuCommandMode: Int, configMaster: IConfigBean){

    var eventMode = eventMode
    var keyCode = IntegerSetting(keyCode, configMaster)
    var appName = StringSetting(appName, configMaster)
    var mcuCommandMode = IntegerSetting(mcuCommandMode, configMaster)

    companion object {
        fun initEventManager(configMaster: IConfigBean) : EventManager{
            return EventManager(EventMode.NoAssignment, -1, "", -1, configMaster)
        }
    }
}
