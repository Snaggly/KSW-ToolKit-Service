package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.IConfigBean
import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting
import com.snaggly.ksw_toolkit.core.config.custom.IntegerSetting

class McuListener(stopKswService: Boolean, mcuSource: Int, configMaster: IConfigBean) {
    var stopKswService = BooleanSetting(stopKswService, configMaster)
    var mcuSource = IntegerSetting(mcuSource, configMaster)

    companion object {
        fun initMcuListener(configMaster: IConfigBean) : McuListener {
            return McuListener(stopKswService = false, mcuSource = 0, configMaster)
        }
    }
}