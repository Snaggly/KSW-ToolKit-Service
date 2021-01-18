package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.IConfigBean
import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting
import com.snaggly.ksw_toolkit.core.config.custom.IntegerSetting

class McuListener(stopKswServiceOnBoot: Boolean, mcuSource: Int, configMaster: IConfigBean) {
    var stopKswServiceOnBoot = BooleanSetting(stopKswServiceOnBoot, configMaster)
    var mcuSource = IntegerSetting(mcuSource, configMaster)

    companion object {
        fun initMcuListener(configMaster: IConfigBean) : McuListener {
            return McuListener(stopKswServiceOnBoot = false, mcuSource = 0, configMaster)
        }
    }
}