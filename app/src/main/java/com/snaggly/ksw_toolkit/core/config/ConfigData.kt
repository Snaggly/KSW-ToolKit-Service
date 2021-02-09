package com.snaggly.ksw_toolkit.core.config

import com.snaggly.ksw_toolkit.core.config.beans.EventManager
import com.snaggly.ksw_toolkit.core.config.beans.SystemTweaks
import com.snaggly.ksw_toolkit.util.enums.EventManagerTypes

data class ConfigData(
        var systemTweaks: SystemTweaks,
        var eventManagers : HashMap<EventManagerTypes, EventManager>
)
