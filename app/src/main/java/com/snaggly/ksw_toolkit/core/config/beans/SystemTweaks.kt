package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.IConfigBean
import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting
import com.snaggly.ksw_toolkit.core.config.custom.IntegerSetting

class SystemTweaks(startAtBoot: Boolean,
                   kswService: Boolean,
                   autoVolume: Boolean,
                   maxVolume: Boolean,
                   hideTopBar: Boolean,
                   shrinkTopBar: Boolean,
                   dpi: Int,
                   configMaster: IConfigBean) {
    var startAtBoot = BooleanSetting(startAtBoot, configMaster)
    var kswService = BooleanSetting(kswService, configMaster)
    var autoVolume = BooleanSetting(autoVolume, configMaster)
    var maxVolume = BooleanSetting(maxVolume, configMaster)
    var hideTopBar = BooleanSetting(hideTopBar, configMaster)
    var shrinkTopBar = BooleanSetting(shrinkTopBar, configMaster)
    var dpi = IntegerSetting(dpi, configMaster)

    companion object {
        fun initSystemTweaks(configMaster: IConfigBean) : SystemTweaks {
            return SystemTweaks(
                    startAtBoot = true,
                    kswService = true,
                    autoVolume = false,
                    maxVolume = false,
                    hideTopBar = false,
                    shrinkTopBar = false,
                    dpi = 160,
                    configMaster = configMaster
            )
        }
    }
}