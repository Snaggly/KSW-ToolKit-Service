package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.IConfigBean
import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting

class SystemTweaks(hideTopBar: Boolean, shrinkTopBar: Boolean, configMaster: IConfigBean) {
    var hideTopBar = BooleanSetting(hideTopBar, configMaster)
    var shrinkTopBar = BooleanSetting(shrinkTopBar, configMaster)

    companion object {
        fun initSystemTweaks(configMaster: IConfigBean) : SystemTweaks {
            return SystemTweaks(hideTopBar = false, shrinkTopBar = false, configMaster)
        }
    }
}