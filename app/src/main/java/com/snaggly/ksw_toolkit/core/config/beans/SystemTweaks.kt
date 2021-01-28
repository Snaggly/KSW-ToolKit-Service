package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.IConfigBean
import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting

class SystemTweaks(startAtBoot: Boolean,
                   kswService: Boolean,
                   autoVolume: Boolean,
                   maxVolume: Boolean,
                   hideTopBar: Boolean,
                   shrinkTopBar: Boolean,
                   configMaster: IConfigBean) {
    var startAtBoot = BooleanSetting(startAtBoot, configMaster)
    var kswService = BooleanSetting(kswService, configMaster)
    var autoVolume = BooleanSetting(autoVolume, configMaster)
    var maxVolume = BooleanSetting(maxVolume, configMaster)
    var hideTopBar = BooleanSetting(hideTopBar, configMaster)
    var shrinkTopBar = BooleanSetting(shrinkTopBar, configMaster)

    companion object {
        fun initSystemTweaks(configMaster: IConfigBean) : SystemTweaks {
            return SystemTweaks(
                    startAtBoot = true,
                    kswService = true,
                    autoVolume = false,
                    maxVolume = false,
                    hideTopBar = false,
                    shrinkTopBar = false,
                    configMaster = configMaster
            )
        }
    }
}